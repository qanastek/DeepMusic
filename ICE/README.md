Install the system dependencies:

```
xargs sudo apt-get install <packages.txt
```

Install the Python dependenciess:

```
sudo pip install -r requirements.txt
```

To run the demo, first start the server:
```
python server.py
```

In a separate window, start the client:
```
python client.py
```




To run the demo, first start the IceGrid service:

```
icegridnode --Ice.Config=config.grid
```

In a separate window, start the application:

```
icegridadmin --Ice.Config=config.grid -e "application add application.xml"
python client.py
```

Stop a application:

```
icegridadmin --Ice.Config=config.grid
application remove Simple
application list
exit
```





The SSL Certificate was generated with:

```
openssl req -x509 -newkey rsa:4096 -keyout cert.key -out cert.crt -days 365
openssl pkcs12 -export -out cert.pfx -inkey cert.key -in cert.crt
```

The SSL Certificate password is:

```
home
```

## Dependencies

* icegrid.jar: https://mvnrepository.com/artifact/com.zeroc/icegrid/3.7.1