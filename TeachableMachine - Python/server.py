import socket
import os
import sys
from start import make_model

soc = socket.socket(socket.AF_INET,socket.SOCK_STREAM)
print('socket created')
   
port = 8080
host = '192.168.0.103'

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
        
        conn, addr = soc.accept()
        length_of_message = int.from_bytes(conn.recv(2), byteorder='big')
        msg = conn.recv(length_of_message).decode("UTF-8")
        print("Length of Message : ", length_of_message)

        configs = msg.split(',')
        print(configs[0],configs[1],configs[2],configs[3],configs[4],configs[5],configs[6:])
        make_model(conn,configs[0],configs[1],configs[2],configs[3],configs[4],configs[5],configs[6:])

        message_to_send = "Training Completed".encode("UTF-8")
        conn.send(len(message_to_send).to_bytes(2, byteorder='big'))
        conn.send(message_to_send)

        print("Acknowledgement Sent...")
        conn.close()
    except KeyboardInterrupt:
        print(f"closing connection to {addr}.")
        print('shutting down server.......')
        conn.shutdown(socket.SHUT_RDWR)
        conn.close()
        sys.exit()
        break


soc.close()