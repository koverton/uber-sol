# uber-sol
Tools for managing multiple Solace brokers interactively

This package provides components which can execute Solace CLI operations 
on several remote appliances via SSH sessions to those appliances. The 
basic operations available are:
* Create and cache SSH and SFTP sessions
* Disconnect cached SSH and SFTP sessions
* Execute ad hoc CLI commands in SSH sessions
* Read CLI scripts from local files and execute them in SSH sessions
* Upload/download files over SFTP sessions

The most interesting thing about this interface is the use of 
'named connections' and globbing on these connection names. This 
allows any script or ad hoc command to be executed across multiple 
SSH sessions.

For example, if I have opened sessions to multiple appliances and named 
those sessions `jimmy`, `jack` and `gunther`, then by passing in a 
connection-expression of `j*` to the `CLIExecutor.execute()` 
or `SFTPExecutor.execute()` method allows me to execute my command on 
sessions `jimmy` and `jack`. If I passed `*` the command would execute 
across all live sessions.


## Connection Command Summary

### Connect to CLI:

The `+` operator opens an SSH connection:

	ubersol> [name] + <ssh-address> <cli-user>
	- Creates a new CLI session and names it for future use (you'll be prompted for password)

	EX:
	ubersol> s01 + solace01.lcs.mit.edu kovadmin

### Connect to SFTP:

the `*` operator opens an SFTP connection:

	ubersol> [name] * <sftp-address> <sftp-user>
	- Creates a new SFTP session and names it for future use (you'll be prompted for password)

	EX:
	ubersol> east1ftp * ec2-54-90-102-251.compute-1.amazonaws.com:2222 ftpuser


### Disconnect:

The `-` operator closes all matching CLI and SFTP connections:

	ubersol> [name] -
	- Disconnects a named shell and removes it from the available shells

	EX:
	ubersol> s01 -


### List all connections:

The `ls` command lists all cached CLI and SFTP connections:

	ubersol> ls
	- Displays a list of all cached connections

	EX:
	ubersol> ls
		Showing all connections ...
		CLI> [ local=>localhost:2222, aws=>54.90.102.251:2222 ]
		SFTP> [ lftp=>localhost:2222 ]


### Exiting the shell:

And if you want to leave, just type:

	ubersol> exit


## CLI Execution Summary

### Run Command
	ubersol> [name] [Command ...]
	- Issues a Solace CLI command on a named session

	EX:
	ubersol> s01 show ip vrf msg-backbone
	       -- or --
	ubersol> s*  show ip vrf msg-backbone

### Run Script
	ubersol> [name] ! [Script Filename]
	- Reads a local CLI script and executes it in the named shell(s) line by line

	EX:
	ubersol> dev* ! ../scripts/create-vpn.cli


## SFTP Execution Summary

### Download Files
The `<` command downloads files from SFTP sessions:

	ubersol> [name] < <remote-path> <local-path>
	- Downloads file(s) according to the remote-path and stores them according to the local-path

	EX:
	ubersol> dev* < logs/*.log .

### Upload Files
The `>` command uploads files to SFTP sessions:
	ubersol> [name] > <remote-path> <local-path>
	- Uploads file(s) according to the local-path and stores them according to the remote-path

	EX:
	ubersol> dev* > soltr_7.0.0.1078.tar.gz loads/

### List Files
	ubersol> [name] ls <remote-path>
	- Lists files in the SFTP server according to the remote-path

	EX:
	ubersol> dev* ls logs/*.log
	