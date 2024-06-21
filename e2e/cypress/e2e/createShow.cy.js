context("create show", () => {
    beforeEach(() => {
        cy.loginAdmin();
    });

    function visitShowCreation() {
        return cy.visit('/#/showcreation');
    }

    function submit() {
        return cy.get('[data-testid="create-show-btn"]').click();
    }

    function eventInput() {
        return cy.get('[data-testid="search-event-input"] input');
    }

    function locationInput() {
        return cy.get('[data-testid="search-location-input"] input');
    }

    function datetimeInput() {
        return cy.get('[data-testid="datetime-input"]');
    }

    function artistInput() {
        return cy.get('[data-testid="search-artist-input"] input');
    }

    function autocompleteItem() {
        return cy.get('[data-testid="autocomplete-item"]:first');
    }

    function priceInputs() {
        return cy.get('[data-testid="price-input"]');
    }

    describe("creating a new show with predefined event and location", () => {
        it("should prompt if not all fields are filled out", () => {
            visitShowCreation();
            submit();
            cy.contains("Bitte füllen Sie das Form komplett aus.");
        });

        it("should create the show, making it available for selection and add it to the event entry", () => {
            const event = "create-show-test-event-title-001";
            const location = "create-show-test-location-name-001";
            const artist = "create-show-test-artist-artistname-001";
            const price = "20";

            visitShowCreation();

            eventInput().type(event);
            autocompleteItem().click();

            locationInput().type(location);
            autocompleteItem().click();

            const in30min = new Date((new Date()).getTime() + 30 * 60000);

            function pad(str) {
                return ('0' + str).slice(-2);
            }

            datetimeInput().type(`${in30min.getFullYear()}-${pad(in30min.getMonth() + 1)}-${pad(in30min.getDate())}T${pad(in30min.getHours())}:${pad(in30min.getMinutes())}`);

            artistInput().type(artist);
            autocompleteItem().click();

            priceInputs()
                .each($input => {
                    cy.wrap($input)
                        .type(price);
                })

            submit();

            cy.contains("Vorführung erfolgreich erstellt.");

            cy.visit('/#/search');
            cy.get('[data-testid="search-by-event"]').click();
            cy.get('[data-testid="event-search-input"]')
                .type(event)
                .wait(500);
            cy.get('[data-testid="event-details-btn"]').click();

            cy.contains(event);
            cy.contains("create-show-test-event-description-001");
            cy.contains("Tickets ab: 20€");
        });
    });
    describe("creating a new event or location in the show-creation view", () => {
        it('should fill in the event or location after creating it', () => {
            visitShowCreation();

            const eventTitle = "testing new event e2e 001";

            cy.get('[data-testid="create-event-btn"]').click();
            cy.get('[data-testid="event-title-input"').type(eventTitle);
            cy.get('[data-testid="event-duration-input"').type("20");
            cy.get('[data-testid="event-type-input"').select("THEATER");
            cy.get('[data-testid="event-description-input"').type("some desc");
            cy.get('[data-testid="event-submit"').click();
            cy.url().should('include', '/#/showcreation');

            eventInput().should('have.value', eventTitle);

            const locationName = "testing new location e2e 001";

            cy.get('[data-testid="create-location-btn"]').click();
            cy.get('[data-testid="location-name-input"]').type(locationName);
            cy.get('[data-testid="location-street-input"]').type("some street");
            cy.get('[data-testid="location-zip-input"]').type("1234");
            cy.get('[data-testid="location-city-input"]').type("Niemandsland");
            cy.get('[data-testid="location-hallplan-input"] input').type("create-show-test-hallplan-001");
            autocompleteItem().click();

            cy.get('[data-testid="location-submit"]').click();

            locationInput().should('have.value', locationName);

            cy.reload();

            eventInput().should('have.value', eventTitle);
            locationInput().should('have.value', locationName);
        });
    });
})