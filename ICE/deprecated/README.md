# The SSL Certificate was generated with

```bash
openssl req -x509 -newkey rsa:4096 -keyout cert.key -out cert.crt -days 365
openssl pkcs12 -export -out cert.pfx -inkey cert.key -in cert.crt
```

The SSL Certificate password is:

```bash
home
```
