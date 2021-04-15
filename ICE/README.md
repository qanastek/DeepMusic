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







Note that this demo uses port 10000. If port 10000 is not available on your
machine, you need to edit both client and server to use a free port.

The demo also assumes the client and server are running on the same host.
To run the demo on separate hosts, edit the server to remove `-h localhost`
from the object adapter's endpoint, and edit the client to replace `localhost`
with the host name or IP address of the server.

To run the server with web socket support:
```
python server.py --Ice.Default.Protocol=ws
```

To run the client with web socket support:
```
python client.py --Ice.Default.Protocol=ws
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