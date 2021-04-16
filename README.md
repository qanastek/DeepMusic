<p align="center">
   <img src="GithubAssets/Logo.png" width="60%">
</p>

DeepMusic is an easy to use Spotify like app to manage and listen to your favorites musics.

Technically, this is an Android Application and its entire RPC / MOM backend.

<!-- ![Preview](GithubAssets/Preview.png) -->
## Install Dependencies
* `sudo apt-get install vlc`
* `pip install -r requirements.txt`

## Another Dependency for Compiling Android on Windows
* Install the Zeroc Ice 3.7 for Java [here](https://zeroc.com/downloads/ice/3.7/java)

## Server Installation
Here is the procedure to install this software :
1. Run MySQL Server
   1. Linux: `sudo systemctl start mysql`
   2. Windows: Start [XAMPP](https://www.apachefriends.org/fr/index.html)
2. Run: `icegridnode --Ice.Config=config.grid`

## Client Installation
Here is the procedure to install this software :
1. Android
   1. Download the **.APK** in the release section
   2. Install it on your Android device
   3. Run it when the server is started
2. Python, Run it using
   1. `icegridadmin --Ice.Config=config.grid -e "application add application.xml"`
   2. `python client.py`

## References
* [Dribble](https://dribbble.com/)
* [Behance](https://www.behance.net/)
* [Illustrations](https://undraw.co/illustrations)
* [Some Icons](http://flaticon.com/)
* https://www.w3schools.com/python/python_mysql_delete.asp
* https://search.maven.org/search?q=g:com.zeroc%20AND%20v:3.7.5