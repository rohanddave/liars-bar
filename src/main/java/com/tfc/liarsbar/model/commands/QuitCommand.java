package com.tfc.liarsbar.model.commands;

import com.tfc.liarsbar.model.actions.QuitAction;

public class QuitCommand extends AbstractGameCommand {

    public QuitCommand(CommandRequest request) {
        super(request, new QuitAction());
    }

    @Override
    public String getDescription() {
        return "Quit the game";
    }

    @Override
    public String getCommandName(){
        return "quit";
    }
}