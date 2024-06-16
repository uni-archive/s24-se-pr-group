declare namespace Cypress{
    interface Chainable {
        /**
         * Navigate to main page and login as admin
         */
        loginAdmin();

        /**
         * Navigate to main page and login as given user
         */
        loginUser(username: string, password: string);

        /**
         * Navigates to the order details page and cancels the order.
         */
        cancelOrder(orderId: number);

        /**
         * Creates a message with a given text
         * @param msg the text of the created message
         */
        createMessage(msg: string);


        gotoLogin();

        fillLoginForm(username: string, password: string);
        clearLoginForm();
    }
}
