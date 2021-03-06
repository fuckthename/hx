package hx.utils;

import java.util.HashMap;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.registry.LanguageRegistry;

import net.minecraft.item.Item;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.Configuration;
public class ItemLoader
{
    int itemID;
    private Item theItem;
    private String name;
    private HyperMod mod;
    //	//accessor
    //============================================================================

    public int id()
    {
        return itemID;
    }

    public Item item()
    {
        return theItem;
    }

    //constructor
    //============================================================================

    public ItemLoader(HyperMod mod, String name)
    {
        this.mod = mod;
        this.name = name;
    }

    //steps
    //============================================================================

    public void preInit(Configuration config)
    {
        itemID = config.getItem(name, mod.availableItemId()).getInt();
        if(HyperMod.USED_ID.contains(itemID))
        {
        	for(ItemLoader il : mod.itemLoaders.values())
        	{
        		if(il.itemID == itemID)
        		{
        			il.itemID = mod.availableItemId();
        			HyperMod.USED_ID.add(il.itemID);
        			break;
        		}
        	}
        }else
        	HyperMod.USED_ID.add(itemID);
        FMLLog.getLogger().finest("Using " + itemID + " for item " + name);
    }

    public void load()
    {
        try
        {
        	if(itemID <= 0)
        	{
        		FMLLog.getLogger().fine("Item "+name+"disabled through config.");
        		return;
        	}
        	
        	String pathName = mod.getClass().getName();
        	pathName = pathName.substring(0,pathName.lastIndexOf("."));
            Class itemClass = Class.forName(pathName + "." + "Item" + name);
            theItem = (Item) itemClass.getConstructor(int.class).newInstance(itemID);
            String dispName = "";

            for (String word : name.split("(?<!^)(?=[A-Z])"))
            {
                dispName += word + " ";
            }

            dispName = dispName.substring(0, dispName.length() - 1);
            LanguageRegistry.addName(theItem, dispName);
            FMLLog.getLogger().finest("Item " + name + " registered.");
        }
        catch (Exception e)
        {
            FMLLog.getLogger().severe("Item " + name + " class NOT FOUND!");
        }
    }

	public void registerRenderer() {
		if(itemID <= 0)return;
		
		if(FMLCommonHandler.instance().getSide().isServer())return;
		String pathname = mod.getClass().getName();
    	pathname = pathname.substring(0,pathname.lastIndexOf("."));
		try{
			Class itemRender = Class.forName(pathname + "." + "Item" + name + "Renderer");
			MinecraftForgeClient.registerItemRenderer(this.item().itemID, (IItemRenderer) itemRender.newInstance());
		}catch(Exception e)
		{
			FMLLog.getLogger().fine("item renderer skipped for " + name);
		}
	}
}
