To run the project see the guide in AWS.txt 

To start the server on the "local machine", you can execute
 * java -jar ./jars/WoCoServer.jar 127.0.0.1 12345 true 1 

After starting the server in one terminal, you can start a client on the same machine by executing:
 * java -jar ./jars/WoCoClient.jar 127.0.0.1 12345 100 1 1 

Both executables can be stopped with Control+C (on linux).


## PRogram description  
#### WoCoClient  
contains code starting a client and logging resposne times and throughput that client sees.  
Usage: <servername> <serverport> <documentsize(KiB)> <opcount(x100)> <filesuffix> [<seed>] [<clientID>] [<numberOfClients>] [<cleaning>] [<threadcount>] [<repeatCount>]  

#### WoCoServer  
Setup a server and setup workers.  
Usage: <listenaddress> <listenport> <cleaning> <threadcount> [<numberOfClients>] [<documentsize(KiB)>] [<filesuffix>]  [<repeatCount>]


#### Server  
Combines messages received in HashMap<Integer, StringBuilder> buffer, and when it is fully combined it puts it into LinkedBlockingQueue<LineStorage> linesToCount.  
Keeps track on measurements objecs.  
Done by main thread.

#### Worker  
pulls a LineStorage from server, does the step in the message pipeline.  

#### LineStorage  
Holds document in line, a SocketChannel connected to client and a list of time measurements.  
Contains functions removing tags, doing word coint and sending to client.


## Experiments
For experiment in part 2: experiment-threads-server.sh   and experiment-threads-client.sh,  with arguments as described in AWS.txt.  
For experiment in part 3: experiment-documents-server.sh and experiment-documents-client.s, with arguments as described in AWS.txt.  

## making graphs
AWS.txt shows how to download logs to Graphs folder, then move it to experiments-threads or experiments-documents-threads8-clients24 folder.  
Graphs\ConcatFile.sh is helperfunction for summing lines giving more manageable input sizes.  
The txt files in experiments-threads and experiments-documents-threads8-clients24, contains descriptions on how to get the info from logs to the excel sheets.  
Sheets are split up in response time and throughput, file 1 and file 2 and for those also showing standard deviation then an extra sheet is pressent ending in -stds, because of fun.  
queue.xlsx contains sheets for the M/M/m model.


