# Food Ordering System

## 1.0 Overview
This is a **Food Ordering System** developed as part of the Object-Oriented Programming coursework at **Asia Pacific University of Technology & Innovation (APU)**. The system is designed to streamline the food ordering process in a food court environment by connecting customers, vendors, administrators, and delivery runners.

## 2.0 Features
The application includes the following core features:
- **Login access**: User authentication with different roles (Vendor, Customer, Delivery Runner, Administrator).
- **User Registration**: New users can sign up and access the system.
- **Menu Management**: Vendors can manage food items available for order.
- **Food Order Placement**: Customers can place orders for dining, takeaway, or delivery.
- **Notification System**: Alerts for order status updates.
- **Payment System**: Credit-based payment method with admin-controlled top-ups.
- **Order History**: Users can view past orders.
- **Delivery System**: Coordination between vendors, delivery runners, and customers.

## 3.0 Technologies Used
- **Programming Language**: Java (JDK 21)
- **Architecture**: Model-View-Controller (MVC)
- **Data Storage**: Text file-based storage system
- **Development Tools**: IntelliJ IDEA / Eclipse / VS Code (Any Java-compatible IDE)
- **Version Control**: GitHub

## 4.0 Installation & Setup
### 4.1 Prerequisites
Ensure that you have the following installed on your system:
- **JDK 21** ([Download JDK](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html))
- **Git** ([Download Git](https://git-scm.com/downloads))

### 4.2 Clone the Repository
```sh
$ git clone https://github.com/YOUR_GITHUB_USERNAME/food-ordering-system.git
$ cd food-ordering-system
```

### 4.3 Compile & Run the Application
#### Using Command Line
```sh
$ javac -d bin -sourcepath src src/com/java_assignment/group/Main.java
$ java -cp bin com.java_assignment.group.Main
```

#### Using an IDE
1. Open the project in IntelliJ IDEA, Eclipse, or VS Code.
2. Ensure the JDK is set to **JDK 21**.
3. Navigate to `Main.java` and run the application.

## 5.0 Usage
### 5.1 User Roles & Permissions
| User Role       | Functionalities |
|----------------|----------------|
| **Customer** | Register, Browse Menu, Place Orders, Make Payments, View Order History |
| **Vendor** | Manage Menu, Receive Orders, Update Order Status |
| **Delivery Runner** | View Delivery Requests, Update Delivery Status |
| **Administrator** | Manage Users, Reload Customer Credits |

### 5.2 Sample Execution Flow
1. **Login/Register**: Users must log in or register before accessing the system.
2. **Browse Menu**: Customers can select food items from different vendors.
3. **Place Order**: Customers can confirm orders and select dining, takeaway, or delivery.
4. **Payment**: Customers use the in-app credit system for payments.
5. **Order Processing**: Vendors receive order notifications and prepare food.
6. **Delivery**: Delivery runners are notified and complete deliveries.
7. **Order Completion**: Customers receive notifications when orders are ready.

## 6.0 Project Structure
```plaintext
food-ordering-system/
│── src/
│   ├── com/java_assignment/group/
│   │   ├── Controller/    # Handles business logic
│   │   ├── Model/         # Data structure and logic
│   │   ├── View/          # UI components
│   │   ├── Data/          # Text files storing application data
│   │   ├── Main.java      # Entry point of the application
│── README.md              # Project documentation
│── bin/                   # Compiled Java classes
│── .gitignore             # Git ignore rules
```

## 7.0 References
- [Java 21 Documentation](https://docs.oracle.com/en/java/javase/21/)
- [GitHub Guide](https://guides.github.com/)

## 8.0 License
This project is for educational purposes and follows the standard MIT license.

