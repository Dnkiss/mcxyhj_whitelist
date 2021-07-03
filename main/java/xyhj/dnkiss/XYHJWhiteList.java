package xyhj.dnkiss;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class XYHJWhiteList extends JavaPlugin implements Listener, CommandExecutor {

    private String filename = "D:/code.txt";
    private List<String> code = new ArrayList<>();
    File codeFile = new File(filename);

    @Override
    public void onEnable(){
        System.out.println("星夜幻境白名单系统启动");
        saveDefaultConfig();
        Bukkit.getPluginManager().registerEvents(this,this);
        Bukkit.getPluginCommand("wl");
        code = getCode();
    }
    @Override
    public void onDisable(){
        System.out.println("星夜幻境白名单系统正常关闭");
        saveDefaultConfig();
        codeWriter(filename);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if(!(sender instanceof Player)){
            sender.sendMessage("命令仅由玩家使用");
            return true;
        }
        Player p = (Player)sender;
        if(p.getGameMode()==GameMode.SPECTATOR){
            if(args.length==0){
                p.sendMessage("输入 /wl 验证码 进行验证");
            }
            else if(code.contains(args[0])){
                getConfig().set(p.getName(),1);
                saveConfig();
                p.setGameMode(GameMode.SURVIVAL);
                p.sendMessage("验证成功，欢迎加入星夜幻境！");
                code.remove(args[0]);
                codeWriter(filename);
            }
            else if(!code.contains(args[0])){
                p.sendMessage("请输入正确的验证码");
            }
            else{
                p.sendMessage("输入 /wl 验证码 进行验证");
            }
        }
        else{
            p.sendMessage("你已通过正版验证");
        }
        return true;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        String name = e.getPlayer().getName();
        if(getConfig().contains(name)){
            if(getConfig().getInt(name)==0){
                e.getPlayer().setGameMode(GameMode.SPECTATOR);
            }
            else{
                e.getPlayer().setGameMode(GameMode.SURVIVAL);
            }
        }
        else{
            e.getPlayer().setGameMode(GameMode.SPECTATOR);
        }
    }

    public List<String> getCode(){
        List<String> temp = new ArrayList<>();
        String str;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream
                    (new File("D:/code.txt")),
                    StandardCharsets.UTF_8));
            while((str = br.readLine()) != null){
                temp.add(str);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return temp;
    }

    public void codeWriter(String filename){
        try {
            FileOutputStream fs = new FileOutputStream(new File(filename));
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(filename)));
            for(int i = 0; i < code.size(); i++){
                bw.write(code.get(i));
                bw.newLine();
            }
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
