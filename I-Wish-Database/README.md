# I-Wish Project - Database & Database Layer

This repository contains the database schema and the Database Access Object (DAO) layer for the I-Wish desktop application.

## 📁 Directory Structure
- **sql_script/**: Contains the MySQL database creation script (`i_wish_db.sql`) along with tables, foreign keys, and test data.
- **src/**: Contains all Java DAO classes (`UserDAO`, `FriendDAO`, `WishlistDAO`, `ContributionDAO`) and database connection configurations (`DatabaseConnection.java`).
- **pom.xml**: Maven configuration file containing required dependencies (such as MySQL Connector).

## 🚀 How to Run & Setup
1. **Database Setup:** 
   - Run the SQL script located in `sql_script/i_wish_db.sql` in your MySQL environment to create the database and tables.
   - Open `DatabaseConnection.java` and update the database password (`Omar#2005`) if it differs from your local MySQL setup.
2. **Project Integration:**
   - Import the `src` folder and `pom.xml` into your Java IDE (e.g., IntelliJ IDEA or NetBeans) to integrate the database layer with the Server and GUI.