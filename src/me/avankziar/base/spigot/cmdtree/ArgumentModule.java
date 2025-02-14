package me.avankziar.base.spigot.cmdtree;

import java.io.IOException;

import org.bukkit.command.CommandSender;

import me.avankziar.base.general.cmdtree.ArgumentConstructor;
import me.avankziar.base.general.cmdtree.BaseConstructor;

public abstract class ArgumentModule
{
	public ArgumentConstructor argumentConstructor;

    public ArgumentModule(ArgumentConstructor argumentConstructor)
    {
       this.argumentConstructor = argumentConstructor;
       BaseConstructor.getArgumentMapSpigot().put(argumentConstructor.getPath(), this);
    }
    
    //This method will process the command.
    public abstract void run(CommandSender sender, String[] args) throws IOException;

}
