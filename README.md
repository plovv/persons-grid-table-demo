# persons-grid-table-demo

A demo app, that loads and displays a list of persons in a grid table.

Some basic functionalities are provided:
    - The user can filter the table by the First or Last name.
    - It is possible to order the table by a specific column (asc or desc).
    - Select a row in the table to display the person's info.

SQLite db is used to store and retrieve the data.

The table is created dynamically by receiving a list of column names and rows at runtime.
By receiving the column names, the app will use them to create the header of the table. The rows are lists of data presented in the same order of the column names.

The project's architecture is based on the MVP pattern.

After the installation of the app, the ExampleInstrumentedTest can be executed to populate the database with dummy data.