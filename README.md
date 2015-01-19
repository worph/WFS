# WFS
This library aims to provide a light Virtual File System that can exist through various ByteArray implementation like RAM or File.

It provide some interface to abstract the concept of FileSytem (WBasicFileSystem) and byte container (BytePack).

It is also compatible with regular Java interfaces such as :
>1. java.nio.ByteBuffer
>2. java.nio.channels.FileChannel http://docs.oracle.com/javase/7/docs/api/java/nio/channels/FileChannel.html

# Usage
```java
  //select a file that will contain the virtual file system
  File file = new File("./vfsbinaryfile");
  file.createNewFile();
  BytePack test = new MemoryMapedFile(file.toURI(), 50000);
	
  //create the virtual file system
  //ForceCreate will initialize the file system use worphFileSytem = new WFS(test); if its already created in this file 
  WBasicFileSystem worphFileSytem = new WFS(test,WFS.OpenMode.ForceCreate);

  //create a file (this will return an identifier of the created file - in the basic implementation of this file system there is no concept of path)
  int fileid1 = worphFileSytem.createFile();
	
  //you can then delete the file with the following line
  //worphFileSytem.deleteFile(fileid2);
	
  //using the file is easy you just have to open a FileChannel on you will be able to use it like any other java FileChannel
  FileChannel fileChannel = worphFileSytem.newFileChannel(fileid1, EnumSet.<StandardOpenOption>of(StandardOpenOption.WRITE));
  OutputStream outputStream = Channels.newOutputStream(fileChannel);
  outputStream.write(data);
  fileChannel.close();
	
  //you can do the same to read data
  fileChannel = worphFileSytem.newFileChannel(fileid1, EnumSet.<StandardOpenOption>of(StandardOpenOption.READ));
  InputStream inputStream = Channels.newInputStream(fileChannel);
	byte[] read = new byte[data.length];
	inputStream.read(read);
  fileChannel.close();
	
  //close the fileSystem wen you finnish
  worphFileSytem.close();
```
			
