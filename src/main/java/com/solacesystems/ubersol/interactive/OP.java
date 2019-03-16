package com.solacesystems.ubersol.interactive;

enum OP {
    // Connections
    CLIConnect,
    SFTPConnect,
    Disconnect,
    ShowConns,
    // CLI types
    ExecCLI,
    RunCLIScript,
    // SFTP types
    SftpGet,
    SftpPut,
    SftpShow,
    // General types
    Error,
    Help,
    Exit
}
