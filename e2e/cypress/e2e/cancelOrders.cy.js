context("cancel orders", () => {
    beforeEach(() => {
        cy.loginUser("cancelorder-user@email.com", "password");
    })

    describe("cancelling a valid empty order", () => {
        it("marks the order as cancelled", () => {
            const orderId = 1;
            cy.cancelOrder(orderId);
            cy.contains("Storno-Rechnung drucken");
            cy.visit("/#/my/orders");
            cy.get(`th[data-testid="order-id-${orderId}"]`)
                .parent()
                .contains("Ja");
        });
    });

    describe("cancelling a valid non-empty order", () => {
        it("marks the order as cancelled", () => {
            const orderId = 2;
            cy.cancelOrder(orderId);
            cy.contains("Storno-Rechnung drucken");
            cy.visit("/#/my/orders");
            cy.get(`th[data-testid="order-id-${orderId}"]`)
                .parent()
                .contains("Ja");
        });
    });

    describe("cancelling an order with expired cancellation-period", () => {
        it("does not mark it as cancelled and shows error", () => {
            const orderId = 3;
            cy.cancelOrder(orderId);
            cy.contains("Die Bestellung darf nicht storniert werden");
            cy.contains("Storno-Rechnung drucken").should('not.exist');
            cy.get(":visible").contains("Bestellung stornieren");
            cy.visit("/#/my/orders");
            cy.get(`th[data-testid="order-id-${orderId}"]`)
                .parent()
                .contains("Nein");
        });
    });

    describe("cancelling an order that includes a show that already started", () => {
        it("does not mark it as cancelled and shows error", () => {
            const orderId = 5;
            cy.cancelOrder(orderId);
            cy.contains("Die Bestellung darf nicht storniert werden");
            cy.contains("Storno-Rechnung drucken").should('not.exist');
            cy.get(":visible").contains("Bestellung stornieren");
            cy.visit("/#/my/orders");
            cy.get(`th[data-testid="order-id-${orderId}"]`)
                .parent()
                .contains("Nein");
        });
    });
})