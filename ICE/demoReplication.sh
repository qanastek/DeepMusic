icegridregistry --Ice.Config=config.master&
icegridregistry --Ice.Config=config.replica1&
icegridregistry --Ice.Config=config.replica2&
icegridnode --Ice.Config=config.node1&
icegridnode --Ice.Config=config.node2&

icegridadmin --Ice.Config=config.client -e "application add application.xml"
client