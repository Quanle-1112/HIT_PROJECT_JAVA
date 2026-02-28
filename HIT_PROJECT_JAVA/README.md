# WOWTruyen

This is an online comic reading application that allows you to read comics and search for your favorite ones. It helps you organize and track the comics you are currently reading, bookmark your favorites, and even interact with an AI assistant.

## Installation

### Require

- Java 17+
- JavaFX 17+
- Maven
- MySQL Server
- IDE: IntelliJ IDEA, Eclipse, or Netbeans.

### How to run the project?

- Clone the project.
- Set up the database: Create a MySQL database named `WOWTruyen` and execute the provided SQL script to create the necessary tables.
- Update the database credentials in `DBConnect.java` and email configuration (for OTP) in `EmailUtils.java`.
- Install the necessary Maven libraries.
- Run the project from the `Launcher.java` or `Main.java` class.

## How to Use

1. Ensure you are connected to the internet.
2. Run the project in the main class.
3. If you don't have an account, create one (supports email OTP verification).
4. Log in using your account credentials.
5. Retrieve your account password via email if needed.

<p>
    <img src="demo_user/1.png" width="250">
    <img src="demo_user/3.png" width="250">
    <img src="demo_user/2.png" width="250">
    <img src="demo_user/3.5.png" width="250">
</p>

### Home Screen

- You can discover comics from the home screen.
  - View lists of New Updates, Completed Comics, and Coming Soon.
  - Browse comics by category.
- You can search for comics using the search bar.
- You can view more comics or categories by clicking on the respective "Xem thêm" (View More) buttons.

<p>
    <img src="demo_user/4.png" width="250">
    <img src="demo_user/5.png" width="250">
</p>

### View More

<p>
    <img src="demo_user/13.png" width="250">
    <img src="demo_user/14.png" width="250">
    <img src="demo_user/15.png" width="250">
    <img src="demo_user/16.png" width="250">
</p>

### Search Book

<p>
    <img src="demo_user/17.png" width="250">
</p>

### History & Favorite Screen

- **History:** You can see a list of recently read comics. The app automatically saves the latest chapter you were reading.
- **Favorite:** You can save and manage a list of your bookmarked comics.

<p>
    <img src="demo_user/6.png" width="250">
    <img src="demo_user/7.png" width="250">
</p>

### Reading Screen

- You can view detailed information about a comic (author, status, categories, chapter list).
- Read chapters with an optimized interface, smooth scrolling, and quick chapter navigation (Next/Previous).

<p>
    <img src="demo_user/18.png" width="250">
    <img src="demo_user/19.png" width="250">
    <img src="demo_user/20.png" width="250">
</p>

### ChatBox AI

- **ChatBox AI:** Interact with the integrated Gemini AI assistant for comic recommendations or general questions.
<p>
    <img src="demo_user/21.png" width="250">
</p>

### Account Screen

- You can view your personal information, including your avatar, full name, and email address.
- You can manage and update your profile:
  - **Change Avatar:** Upload a new image from your device to set as your profile picture.
  - **Change Name:** Update your display name.
  - **Change Password:** Securely update your account password.
  - **Delete Account:** Delete account information.
- You can safely log out of your current session.

<p>
    <img src="demo_user/8.png" width="250">
    <img src="demo_user/9.png" width="250">
    <img src="demo_user/10.png" width="250">
    <img src="demo_user/11.png" width="250">
    <img src="demo_user/12.png" width="250">
</p>

### Admin Dashboard

- **Access Restriction:** This section is exclusive to accounts with the `ADMIN` role.
- **User Management:** You can view the list of all registered users, search for specific users by ID, username, or email, and easily lock (ban) or unlock accounts to maintain community guidelines.
- **Book Management:** You can browse and search for comics fetched from the API. Admins have the authority to hide specific inappropriate comics from the system, ensuring they are no longer visible to regular users.
- **System Statistics:** You can monitor the platform's overall metrics through visual charts, including a bar chart showing user growth over time and a pie chart illustrating the distribution of comic categories.

<p>
    <img src="demo_admin/1.png" width="250">
    <img src="demo_admin/2.png" width="250">
    <img src="demo_admin/3.png" width="250">
    <img src="demo_admin/4.png" width="250">
    <img src="demo_admin/5.png" width="250">
    <img src="demo_admin/6.png" width="250">
    <img src="demo_admin/7.png" width="250">
    <img src="demo_admin/8.png" width="250">
    <img src="demo_admin/9.png" width="250">
    <img src="demo_admin/10.png" width="250">
</p>

## Technologies & Libraries

### UI & Architecture

- **JavaFX** – Used for building the desktop graphical user interface.
- **MVC Pattern** – Separates the application into Model, View, and Controller components.

### API Integration

- **OkHttp3** – A synchronous and asynchronous HTTP client used for calling OtruyenAPI and Gemini API.
- **Gson** – A library used for converting JSON data from APIs into Java objects.

### Database

- **MySQL Connector/J** – A JDBC driver for MySQL database connectivity.

### Email Handling

- **Jakarta Mail / Angus Mail** – Libraries used for sending OTP verification and password recovery emails.

### Security & Authentication

- **JBCrypt** – A library used for password hashing and secure user authentication.

### Concurrency & Multi-threading

- **JavaFX Task / CompletableFuture** – Used for performing heavy background tasks (like fetching APIs, loading images, and database queries) without freezing the UI.

## Folder Structure

📁WOWTruyen  
┣📁.idea  
┣📁src/main/java/org/example  
┃ ┣📁api (HTTP communication with external APIs)  
┃ ┣📁app (Main entry points of the application)  
┃ ┣📁constant (Global constants and system messages)  
┃ ┣📁controllers (Controller classes managing UI logic by modules)  
┃ ┣📁dao (Data Access Object classes for MySQL database interaction)  
┃ ┣📁data (Services for fetching and processing comic API data)  
┃ ┣📁exception (Custom exceptions and UI error handlers)  
┃ ┣📁model (Model classes representing database entities and API DTOs)  
┃ ┣📁services (Service interfaces and implementations for core business logic)  
┃ ┗📁utils (Utility classes for encryption, email, image loading, and navigation)  
┣📁src/main/resources  
┃ ┣📁image (Static image resources and icons)  
┃ ┗📁view (FXML UI layouts organized by features)  
┣📜pom.xml (Maven build configuration and dependencies)  
┗📜README.md
