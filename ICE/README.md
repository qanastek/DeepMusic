# Running procedure

## Install the system dependencies

```bash
xargs sudo apt-get install <packages.txt
```

## Install the python dependencies

```bash
sudo pip install -r requirements.txt
```

## To run the server, first start the IceGrid service

```bash
icegridnode --Ice.Config=config.grid
```

## In a separate window, start the client

```bash
icegridadmin --Ice.Config=config.grid -e "application add application.xml"
python client.py
```

## Undeploy the application

```bash
icegridadmin --Ice.Config=config.grid
application remove Simple
application list
exit
```

## Dependencies

* icegrid.jar (Android Only): https://mvnrepository.com/artifact/com.zeroc/icegrid/3.7.1