context("authentication (user-story 2.1.2)", () => {
    const password = "password";
    const customerGettingBruteforcePrevented = {
        username: "authentication-212-user01@email.com",
        password: "wrongpassword"
    };
    const adminGettingBruteforcePrevented = {
        username: "authentication-212-admin01@email.com",
        password: "wrongpassword"
    };

    const customer = {
        username: "authentication-212-user02@email.com",
        password
    };

    const admin = {
        username: "authentication-212-admin02@email.com",
        password
    };

    describe("entering the wrong password 5 times in a row", () => {
        it("locks the account of a customer", () => {
            cy.fixture('settings').then(settings => {
                cy.gotoLogin();

                // after 6 invalid attempts, the user should get blocked
                for (let i = 0; i < 6; i++) {
                    cy.fillLoginForm(customerGettingBruteforcePrevented.username, customerGettingBruteforcePrevented.password);
                    cy.contains('Login failed.')
                        .get('button.close')
                        .click();
                   cy.clearLoginForm();
                }

                // now you shouldn't be able to log-in even if the password is correct
                cy.fillLoginForm(customerGettingBruteforcePrevented.username, password);
                cy.contains('Login failed.');
            });
        });
        it("locks the account of an admin", () => {
            cy.fixture('settings').then(settings => {
                cy.gotoLogin();

                // after 6 invalid attempts, the user should get blocked
                for (let i = 0; i < 6; i++) {
                    cy.fillLoginForm(adminGettingBruteforcePrevented.username, adminGettingBruteforcePrevented.password);
                    cy.contains('Login failed.')
                        .get('button.close')
                        .click();
                    cy.clearLoginForm();
                }

                // now you shouldn't be able to log-in even if the password is correct
                cy.fillLoginForm(adminGettingBruteforcePrevented.username, password);
                cy.contains('Login failed.');
            });
        });
    });

    describe("logging in works for", () => {
        it("a customer, only showing customer-routes in the navbar", () => {
            cy.loginUser(customer.username, customer.password);
            cy.contains("Erstelle Veranstaltung")
                .should('not.exist');

            cy.contains("Erstelle Vorführung")
                .should('not.exist');

            cy.contains("Aufführungsorte")
                .should('not.exist');

            cy.visit('/#/user/home');

            cy.contains("Nutzer erstellen")
                .should('not.exist');

            cy.contains("Nutzer verwalten")
                .should('not.exist');

            cy.contains("Saalplan erstellen")
                .should('not.exist');
        });

        it("an admin, additionally showing admin-routes in the navbar", () => {
            cy.loginUser(admin.username, admin.password);
            cy.contains("Erstelle Veranstaltung");
            cy.contains("Erstelle Vorführung");
            cy.contains("Aufführungsorte");

            cy.visit('/#/user/home');

            cy.contains("Nutzer erstellen");
            cy.contains("Nutzer verwalten");
            cy.contains("Saalplan erstellen");
        });
    })
})