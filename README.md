## DESCRIPTION
This is a simple client-server application developed in Java. Basically a user (so a client) connects to a server via socket and, after a simple login,
he is allowed to buy, return and upload products. The list of products is visible in the main screen and the user can access different sections with simple buttons.

This simple project was an assignment for my University (UNIPR) course "Software engineering", where I had to follow
few requests:
- Use of javafx for UI
- Client communicates with Server through sockets
- Client can request the products list, upload and return a product and send credentials for authentication.
- Server has to responde all the client's requests.

I spent quite a few time on this project and really learnt how to use javafx, FXML files and sockets. 
However, I only implemented really basic and raw features, like a text-file database, a login interface without cryptography features, inefficient algorithms for product-searching and authentication and a basic UI. 

---

## CLIENT POV
After starting the application, the client has to log in filling two simple fields: username and password. 
Once authenticated, the client can navigate through the app using lateral buttons. There are four buttons: 
  - Products (default):
    Shows the list of buyable products. Here a client can buy a product by clicking
    on it and select the option "BUY".

  - Return:
    In this section a client can see it's owned products and, eventually, return        them by clicking their icon and selecting the option "RETURN".

  - Upload:
    In this section a client can upload a product by inserting it's name, ID and        price. If the product's ID is already taken, a message appears on the screen 
    and the client has to choose another one. 

