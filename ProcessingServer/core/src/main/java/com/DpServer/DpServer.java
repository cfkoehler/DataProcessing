package com.DpServer;

import emissary.Emissary;
import emissary.command.EmissaryCommand;
//import emissary.util.GitRepositoryState;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DpServer extends Emissary {
    private static final Logger LOG = LoggerFactory.getLogger(DpServer.class);
    public DpServer(Map<String, EmissaryCommand> cmds) {
        super(cmds);
    }

    public static void main(String[] args) {
        Map<String, EmissaryCommand> cmds = new HashMap<>();
        cmds.putAll(Emissary.EMISSARY_COMMANDS);
        // replace version command, but could also add commands
        EmissaryCommand vCmd = new DpVersionCommand();
        cmds.put(vCmd.getCommandName(), vCmd);
        new DpServer(cmds).execute(args);
    }

//    @Override
//    protected void dumpVersionInfo() {
//        LOG.info(GitRepositoryState.dumpVersionInfo(GitRepositoryState.getRepositoryState("DpServer.git.properties"), "DpServer"));
//        LOG.info(GitRepositoryState.dumpVersionInfo(GitRepositoryState.getRepositoryState("emissary.git.properties"), "Emissary"));
//    }
}