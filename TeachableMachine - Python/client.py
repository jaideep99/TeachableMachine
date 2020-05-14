import socket
import os


soc = socket.socket(socket.AF_INET,socket.SOCK_STREAM)

port = 8080
host = '192.168.0.102'

Ip = socket.gethostname()

try:
    print('shutting down server!!')
    soc.connect((host,port))
    soc.send(bytes('shut down','utf-8'))
    soc.close()
except socket.error as err:
    print(err)
