# codes of server written by TAHERE FAHIMI
# multiThread programming

from socket import *
from threading import Thread

# initial class variable port number and buffer size
BUFFER = 1024
PORTNUM = 4400


if __name__ == "__main__":
    serverSocket = socket(AF_INET, SOCK_DGRAM)
    serverSocket.bind(('',PORTNUM))
    waiting = True
    print("server is up ... ")
    print("ready to accept client ... ")
    while waiting:
        mes, clientAdd = serverSocket.recvfrom(BUFFER)
        print("the mesage is : ")
        print(clientAdd)
        print(mes)
        #serverSocket.sendall()
        serverSocket.sendto(mes, clientAdd)