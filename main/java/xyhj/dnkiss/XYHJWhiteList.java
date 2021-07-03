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

public class XYHJWhiteList extends JavaPlugin implements Listener, CommandExecutor {

    private final String codeFilename = "D:/code.txt";
    private final String onlineUserFilename = "D:/onlineUser.txt";
    private List<String> code = new ArrayList<>();
    private List<String> onlineUser = new ArrayList<>();

    @Override
    public void onEnable(){
        System.out.println("星夜幻境白名单系统启动");
        saveDefaultConfig();
        Bukkit.getPluginManager().registerEvents(this,this);
        Bukkit.getPluginCommand("wl");
        code = getList(codeFilename);
    }
    @Override
    public void onDisable(){
        System.out.println("星夜幻境白名单系统正常关闭");
        saveConfig();
        saveDefaultConfig();
        codeWriter(codeFilename);
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
                codeWriter(codeFilename);
            }
            else if(!code.contains(args[0])){
                p.sendMessage("请输入正确的验证码");
                p.sendMessage("请输入/wl 验证码，进行验证");
                p.sendMessage("正版玩家请登录服务器：自动获取验证码");
                p.sendMessage("非正版玩家请加入qq群：750566298申请验证码");
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
        onlineUser = getList(onlineUserFilename);
        if(getConfig().contains(name)){
            if(getConfig().getInt(name)==0){
                if(onlineUser.contains(name)){
                    e.getPlayer().setGameMode(GameMode.SURVIVAL);
                    getConfig().set(name,1);
                    saveConfig();
                }
                else{
                    e.getPlayer().sendMessage("请输入/wl 验证码，进行验证");
                    e.getPlayer().sendMessage("正版玩家请登录服务器：自动获取验证码");
                    e.getPlayer().sendMessage("非正版玩家请加入qq群：750566298申请验证码");
                    e.getPlayer().setGameMode(GameMode.SPECTATOR);
                }
            }
            else{
                e.getPlayer().setGameMode(GameMode.SURVIVAL);
            }
        }
        else{
            if(onlineUser.contains(name)){
                e.getPlayer().setGameMode(GameMode.SURVIVAL);
                getConfig().set(name,1);
                saveConfig();
            }
            else{
                e.getPlayer().sendMessage("请输入/wl 验证码，进行验证");
                e.getPlayer().sendMessage("正版玩家请登录服务器：自动获取验证码");
                e.getPlayer().sendMessage("非正版玩家请加入qq群：750566298申请验证码");
                e.getPlayer().setGameMode(GameMode.SPECTATOR);
            }
        }
    }

    public List<String> getList(String filename){
        List<String> temp = new ArrayList<>();
        String str;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream
                    (new File(filename)),
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
