#### DESCRIPTION
This is a simple client-server application developed in Java. Basically a client connects to a server via socket and, after a simple login,
the client is allowed to buy, return and upload products. The list of products is visible in the main screen and the navigation between sections
is made with simple buttons.

It is my first "complete" project, I spent quite a few time on it and really learnt how to use javafx, FXML files and sockets. 
However, I only implemented really basic and raw features, like a text-file database, a login interface without cryptography and
kind of inefficient algorithms for product-searching and a basic UI. 

I'm overall proud of my work but I'm looking to do better next time.




## CLIENT POV
After starting the application, the client has to log himself in by filling two simple fields: username and password. 
--- image ---
Once authenticated, the client can navigate through the app using lateral buttons. There four buttons: 
  - Products (default):
    Shows the list of buyable products. Here a client can buy a product by clicking
    on it and select the option "BUY".

  - Return:
    In this section a client can see it's owned products and, eventually, return        them by clicking their icon and selecting the option "RETURN".

  - Upload:
    In this section a client can upload a product by inserting it's name, ID and        price. If the product's ID is already taken, a message appears on the screen 
    and the client has to choose another one. 
