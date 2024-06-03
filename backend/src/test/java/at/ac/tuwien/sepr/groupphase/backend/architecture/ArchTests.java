package at.ac.tuwien.sepr.groupphase.backend.architecture;

import at.ac.tuwien.sepr.groupphase.backend.BackendApplication;
import at.ac.tuwien.sepr.groupphase.backend.persistence.mapper.BaseEntityMapper;
import at.ac.tuwien.sepr.groupphase.backend.persistence.mapper.BaseResponseMapper;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.properties.HasName;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.CompositeArchRule;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.util.List;

import static com.tngtech.archunit.base.DescribedPredicate.not;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.GeneralCodingRules.ACCESS_STANDARD_STREAMS;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_USE_FIELD_INJECTION;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_USE_JODATIME;
import static com.tngtech.archunit.library.freeze.FreezingArchRule.freeze;

@AnalyzeClasses(packagesOf = BackendApplication.class, importOptions = ImportOption.DoNotIncludeTests.class)
public class ArchTests {

    @ArchTest
    static final ArchRule layersPattern = freeze(layeredArchitecture()
        .consideringAllDependencies()
        .layer("Endpoint").definedBy("at.ac.tuwien.sepr.groupphase.backend.endpoint..")
        .layer("Service").definedBy("at.ac.tuwien.sepr.groupphase.backend.service..")
        .layer("Persistence").definedBy("at.ac.tuwien.sepr.groupphase.backend.persistence..")
        .whereLayer("Endpoint").mayNotBeAccessedByAnyLayer()
        .whereLayer("Service").mayOnlyBeAccessedByLayers("Endpoint")
        .whereLayer("Persistence").mayOnlyBeAccessedByLayers("Service"));

    @ArchTest
    private final ArchRule no_access_to_standard_streams = NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS;

    @ArchTest
    private void no_access_to_standard_streams_as_method(JavaClasses classes) {
        noClasses().should(ACCESS_STANDARD_STREAMS).check(classes);
    }


    @ArchTest
    private final ArchRule no_java_util_logging = NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING;

    @ArchTest
    private final ArchRule loggers_should_be_private_static_final =
        fields().that().haveRawType(Logger.class)
            .should().bePrivate()
            .andShould().beStatic()
            .andShould().beFinal()
            .because("we agreed on this convention");

    @ArchTest
    private final ArchRule no_jodatime = NO_CLASSES_SHOULD_USE_JODATIME;

    JavaClasses classes = new ClassFileImporter().withImportOptions(List.of(new ImportOption.DoNotIncludeTests())).importPackagesOf(BackendApplication.class);
    JavaClasses allExceptMappers = classes.that(not(JavaClass.Predicates.resideInAPackage("..mapper..")));

    @Test
    void shouldNotUseFieldInjection() {
        NO_CLASSES_SHOULD_USE_FIELD_INJECTION.check(allExceptMappers);
    }

    @ArchTest
    static final ArchRule no_classes_should_access_standard_streams_or_throw_generic_exceptions =
        freeze(CompositeArchRule.of(NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS)
            .and(NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS));

}
