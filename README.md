# ProjektMatematika

A platform enabling math clubs to organize mathematical competitions, maintain records of results, and collaborate on math projects. This project, serving as a CRUD application, is developed for a university class to learn all the key concepts of programming in Java.

## Tasks Implemented:

1. Implementation of classes representing entities used in the project task. Each class must be located in a package with classes that share common properties (e.g., entities must be in one package, and the main class for running the application in another package).
2. Utilization of abstract classes, interfaces, records, sealed interfaces, and the builder pattern design pattern to leverage all object-oriented programming paradigms of the Java programming language.
3. Handling and throwing exceptions in all parts of the program where they can occur. Each exception must be logged using the Logback library. Additionally, it is necessary to create at least two checked and two unchecked exceptions, throw and catch them in the application's source code, and log them using the Logback library. Exception classes must be located in a separate package.
4. Usage of collections such as lists, sets, and maps, along with lambda expressions for filtering and sorting all entities in the application.
5. Use of at least two generic classes in the application, located in a package together with entities. One class must have only one generic parameter, while the other class must have two generic type parameters.
6. Utilization of text files to load data about usernames and passwords during user login to the application. It is necessary to use binary files to serialize and deserialize data about performed changes to the data in the project task (e.g., after entering new data and changing existing data).
7. Implementation of a JavaFX screen for user login to the application, which reads data from a text file about usernames and hashed passwords from the text file created in the sixth step. Each application must have at least two user roles.
8. Implement a JavaFX screen that enables searching and filtering of data for each entity, using the TableView functionality. It should also allow adding new entities, modifying existing entities, and deleting entities. Each action of modifying and deleting entities must involve additional user confirmation using a JavaFX dialog.
9. Implement a JavaFX screen that displays all changes made in the project task application using serialized data from step six. Each change must contain information about the modified data, old and new values, the role that made the change, and the date and time when the change occurred.
10. Create a database containing data about all entities used in the application and implement a class that implements functionalities for creating a connection to the database, executing queries on the database, fetching data from the database, and closing the connection to the database.
11. Use threads to implement functionalities for refreshing data on the application screen and concurrent access to a shared resource accessed by multiple threads through thread synchronization (e.g., one thread prints details about the last modified data fetched from the serialized file, and for another thread that saves new changes to the serialized file, synchronization with the first thread is ensured).

## Demo

![Demo 1](https://github.com/N0ksa/ProjektniZadatakFx/assets/118447696/14872cdf-1df4-4753-a8e5-e1b28245ef08)

## Functionality in the Application:

- **Filtering Club Members**: Users can filter club members based on various criteria such as name, age, grade, etc., to quickly find specific members or groups of members.

- **Adding New Club Members**: The application allows administrators or authorized users to add new members to the math club. Users can input relevant information such as name, age, grade, etc., to create a new member profile.

- **Editing Existing Club Members**: Administrators or authorized users have the ability to edit the details of existing club members. This includes updating information such as name, age, grade, etc., to keep member profiles accurate and up-to-date.

- **Adding New Competitions**: Users can add new mathematical competitions to the platform. They can specify details such as competition name, date, location, and rules to create a new competition entry.

- **Registering Users for Competitions**: Club members or participants can register for upcoming competitions through the application.

- **Accessing Detailed Statistics**: Users can access detailed statistics related to club activities, competitions, and projects. These statistics may include performance metrics, participation rates, project progress, etc. Users have the option to print these statistics for further analysis or reporting purposes.
  
- **Creating New Projects**

![Demo 2](https://github.com/N0ksa/ProjektniZadatakFx/assets/118447696/7d652478-02db-421b-b7a2-eca28aa6b792)

## Additional Functionality:

- **Web View Access for Each Project**: Every project has access to its webpage through a web view within the application, allowing users to view additional information or external resources related to the project.

- **Multiple User Roles**: The application supports two user roles - clubs and admin. Clubs have limited access to certain functionalities, while admin has full access to all features and additional privileges.

- **Admin Visibility of Club Logins and Changes**: Admin users can view detailed statistics regarding club logins and every change made by clubs within the application, ensuring transparency and oversight.

## Author
- Leon Šarko
