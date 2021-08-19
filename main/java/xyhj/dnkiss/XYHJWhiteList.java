package xyhj.dnkiss;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class XYHJWhiteList extends JavaPlugin implements Listener, CommandExecutor {

    private final String codeFilename = "D:/code.txt";
    private final String onlineUserFilename = "D:/onlineUser.txt";
    private List<String> code = new ArrayList<>();
    private List<String> onlineUser = new ArrayList<>();
    private static BossBar bossBar = Bukkit.createBossBar("您现在是观察者模式，请参照群内信息申请白名单"
    , BarColor.RED, BarStyle.SOLID);
    private Location l = null;
    private List<Player> cancelBreakCrop = new ArrayList<>();//防止耕地破坏

    @Override
    public void onEnable(){
        System.out.println("星夜幻境白名单系统启动");
        Bukkit.getPluginManager().registerEvents(this,this);
        Bukkit.getPluginCommand("wl");
        code = getList(codeFilename);
        bossBar.setVisible(true);

    }
    @Override
    public void onDisable(){
        System.out.println("星夜幻境白名单系统正常关闭");
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
                l = p.getLocation();
                l.setX(-551);
                l.setY(73);
                l.setZ(-1205);
                getConfig().set(p.getName(),1);
                saveConfig();
                p.teleport(l);
                p.setGameMode(GameMode.SURVIVAL);
                p.setAllowFlight(false);
                p.setFlying(false);
                p.sendMessage("验证成功，欢迎加入星夜幻境！");
                code.remove(args[0]);
                codeWriter(codeFilename);
                bossBar.removePlayer(p);
            }
            else if(!code.contains(args[0])){
                p.sendMessage("请输入正确的验证码");
                p.sendMessage("请输入/wl 验证码，进行验证");
                p.sendMessage("正版玩家请登录服务器：zhengban.mcxyhj.cn自动获取验证码");
                p.sendMessage("非正版玩家请加入qq群：750566298申请验证码");
            }
            else{
                p.sendMessage("输入 /wl 验证码 进行验证");
            }
        }
        else{
            p.sendMessage("你已拥有白名单");
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
                    e.getPlayer().sendMessage("正版玩家请登录服务器：zhengban.mcxyhj.cn自动获取验证码");
                    e.getPlayer().sendMessage("非正版玩家请加入qq群：750566298申请验证码");
                    e.getPlayer().setGameMode(GameMode.SPECTATOR);
                    e.getPlayer().setFlying(true);
                    e.getPlayer().setAllowFlight(true);
                    bossBar.addPlayer(e.getPlayer());
                }
            }
            else if(getConfig().getInt(name) == 1 || getConfig().getInt(name) == 2){
                e.getPlayer().setGameMode(GameMode.SURVIVAL);
                bossBar.removePlayer(e.getPlayer());
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
                e.getPlayer().setFlying(true);
                e.getPlayer().setAllowFlight(true);
                bossBar.addPlayer(e.getPlayer());
            }
        }

        //登陆世界检测
        if(e.getPlayer().getWorld().getName().equalsIgnoreCase("xyhj1.16.5")){
            e.getPlayer().setGameMode(GameMode.SPECTATOR);
            e.getPlayer().setFlying(true);
            e.getPlayer().setAllowFlight(true);
        }

        //正版权限组
        if(getConfig().contains(name)){
            if(getConfig().getInt(name) == 1 && onlineUser.contains(name)){
                getConfig().set(name,2);
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"manuadd "+name+" zhengban");
            }
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e){
        if(e.getPlayer().getGameMode() == GameMode.SPECTATOR){
            e.getPlayer().setAllowFlight(true);
            e.getPlayer().setFlying(true);
            e.getPlayer().sendMessage("请输入/wl 验证码，进行验证");
            e.getPlayer().sendMessage("正版玩家请登录服务器：自动获取验证码");
            e.getPlayer().sendMessage("非正版玩家请加入qq群：750566298申请验证码");
        }
    }

    //切换世界监测
    @EventHandler
    public void worldChange(PlayerChangedWorldEvent e){
        if(e.getPlayer().getWorld().getName().equalsIgnoreCase("xyhj1.16.5")){
            e.getPlayer().setGameMode(GameMode.SPECTATOR);
            e.getPlayer().setFlying(true);
            e.getPlayer().setAllowFlight(true);
        }else{
            if(getConfig().getInt(e.getPlayer().getName())==1){
                e.getPlayer().setGameMode(GameMode.SURVIVAL);
                e.getPlayer().setFlying(false);
                e.getPlayer().setAllowFlight(false);
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
