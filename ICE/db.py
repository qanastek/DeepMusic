import Ice
Ice.loadSlice('Server.ice')
import Server

import mysql.connector

class DB:

    DB_NAME = "DeepMusic"
    DB_TABLE = "musics"

    mydb = mysql.connector.connect(
        host="localhost",
        user="root",
        password="secret",
        database=DB_NAME
    )

    mycursor = mydb.cursor()

    # Initialize the database structure and content
    def __init__(self):

        print(self.mydb)

        # Create the Database
        self.mycursor.execute("CREATE DATABASE IF NOT EXISTS {}".format(self.DB_NAME))

        # Create the Table
        self.mycursor.execute("""
        CREATE TABLE IF NOT EXISTS {} (
            identifier INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
            titre VARCHAR(255) UNIQUE,
            artiste VARCHAR(255) UNIQUE,
            album VARCHAR(255),
            path VARCHAR(255)
        )""".format(self.DB_TABLE))

        # Base Musics
        musics = [
            Server.Music(1,"Mike D-Sturb & High Voltage","Artiste Test 1","Album Test 1","musics/Checkie Brown - Rosalie (CB 104).mp3"),
            Server.Music(2,"Mike Dee Yan-Key - Hold on","Artiste Test 2","Album Test 2","musics/Lobo Loco - Bad Guys (ID 1333).mp3"),
            Server.Music(3,"Checkie Brown - Rosalie (CB 104)","Artiste Test 3","Album Test 3","musics/Dee Yan-Key - Hold on.mp3"),
        ]

        # Insert Them
        for m in musics:
            self.insertMusic(m)

        # Commit the change
        self.mydb.commit()
        
        print(self.mycursor.rowcount, "record inserted.")

    # Insert a Music in the database
    def insertMusic(self, music):

        # Setup the query
        sql = "INSERT IGNORE INTO " + self.DB_TABLE + " (titre, artiste, album, path) VALUES (%s, %s, %s, %s)"
        
        # Setup the values
        val = (music.titre, music.artiste, music.album, music.path)
        
        # Execute the command
        self.mycursor.execute(sql, val)

    # Update a Music in the database
    def updateMusic(self, music):

        # Setup the query
        sql = "UPDATE " + self.DB_TABLE + " SET titre = %s, artiste = %s, album = %s, path = %s WHERE identifier = %s"
        
        # Setup the values
        val = (music.titre, music.artiste, music.album, music.path, music.identifier)
        
        # Execute the command
        self.mycursor.execute(sql, val)
    
    def insert(self,music):

        # Insert the Music
        self.insertMusic(music)

        # Commit the changes
        self.mydb.commit()

    def update(self,music):

        # Update the Music
        self.updateMusic(music)

        # Commit the changes
        self.mydb.commit()

    # Remove a Music from the database
    def removeMusic(self, identifier):

        # Setup the query
        sql = "DELETE FROM " + self.DB_TABLE + " WHERE identifier = '{}'".format(identifier)
        
        # Execute the command
        self.mycursor.execute(sql)
        
        # Commit the changes
        self.mydb.commit()

        return True

    # Get all the Musics from the database
    def getMusics(self):
                
        # Query the table to get all the musics
        self.mycursor.execute("SELECT * FROM " + self.DB_TABLE)
        
        # Fetch the result
        res = self.mycursor.fetchall()

        # Output Array
        musics = []

        # For each tuple
        for m in res:

            # Convert the tuple into a instance of Music
            musics.append(
                Server.Music(*m)
            )

        # Return all the available Music instances 
        return musics