Cypress.Commands.add('loginAdmin', () => {
    cy.fixture('settings').then(settings => {
        cy.gotoLogin();
        cy.fillLoginForm(settings.adminUser, settings.adminPw);
        cy.contains('Logout');
    })
})

Cypress.Commands.add('loginUser', (username, password) => {
    cy.gotoLogin();
    cy.fillLoginForm(username, password);
    cy.contains('Logout');
});

Cypress.Commands.add('gotoLogin', () => {
    cy.fixture('settings').then(settings => {
        cy.visit(settings.baseUrl);
        cy.contains('a', 'Login').click();
    });
});

Cypress.Commands.add('fillLoginForm', (username, password) => {
    cy.get('input[id="inputUsername"]').type(username);
    cy.get('input[id="password"]').type(password);
    cy.contains('button', 'Login').click();
});

Cypress.Commands.add('clearLoginForm', () => {
    cy.get('input[id="inputUsername"]').clear();
    cy.get('input[id="password"]').clear();
})

Cypress.Commands.add('createMessage', (msg) => {
    cy.fixture('settings').then(settings => {
        cy.contains('a', 'Message');
        cy.contains('button', 'Add message').click();
        cy.get('input[name="title"]').type('title' +  msg);
        cy.get('textarea[name="summary"]').type('summary' +  msg);
        cy.get('textarea[name="text"]').type('text' +  msg);
        cy.get('button[id="add-msg"]').click();
        cy.get('button[id="close-modal-btn"]').click();

        cy.contains('title' +  msg).should('be.visible');
        cy.contains('summary' +  msg).should('be.visible');
    })
})

Cypress.Commands.add('cancelOrder', (orderId) => {
    cy.fixture('settings').then(settings => {
        cy.visit(`/#/my/orders/${orderId}`);
        cy.get(":visible").contains("Bestellung stornieren").click();
        cy.get('app-confirm-cancel-order-dialog')
            .contains("Ja, stornieren")
            .should('be.visible')
            .click();
    })
})
