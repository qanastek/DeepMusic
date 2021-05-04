# Running procedure

## Install the system dependencies

```bash
xargs sudo apt-get install -y <packages.txt
```

## Install the python dependencies

```bash
sudo pip install -r requirements.txt
```

## Run the MySQL database

```bash
sudo /etc/init.d/mysql start
```

## To run the server, first start the IceGrid service

```bash
icegridregistry --Ice.Config=config.master&

icegridregistry --Ice.Config=config.replica1&
icegridnode --Ice.Config=config.node1&

icegridregistry --Ice.Config=config.replica2&
icegridnode --Ice.Config=config.node2&
```

## In a separate window, start the client

```bash
icegridadmin --Ice.Config=config.client -e "application add application.xml"
python client.py
```

## Undeploy the application

```bash
icegridadmin --Ice.Config=config.client -e "application remove Simple"
```

## Dependencies

* icegrid.jar (Android Only): https://mvnrepository.com/artifact/com.zeroc/icegrid/3.7.1