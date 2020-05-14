import socket
import os
import sys
from start import make_model

soc = socket.socket(socket.AF_INET,socket.SOCK_STREAM)
print('socket created')
   
port = 8080
host = '192.168.0.102'

Ip = socket.gethostname()

try:
    soc.bind((host,port))
    soc.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    
except socket.error as err:
    print('bind failed, Error code : '+str(err[0])+ ',Message : ',str(err[1]))
    sys.exit()

print('socket bind success!')
print("server started at : ",Ip)

soc.listen(5)

print("socket listening....")

running = True

while running:
    try:
        clientsocket,address = soc.accept()
        print('connected with '+address[0]+' : '+str(address[1]))
        message = clientsocket.recv(1024).decode()
        print(message)
        configs = message.split(',')
        print(configs)
        make_model(configs[0],configs[1],configs[2],configs[3],configs[4:])

    except KeyboardInterrupt:
        print(f"closing connection to {address}.")
        print('shutting down server.......')
        clientsocket.shutdown(socket.SHUT_RDWR)
        clientsocket.close()
        sys.exit()
        break


soc.close()