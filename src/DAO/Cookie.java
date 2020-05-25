package DAO;

public class Cookie {
    private String username;
    private long time;

    public Cookie(String username){
        this.username=username;
        this.time=System.currentTimeMillis();
    }

    public long getTime() {
        return time;
    }

    @Override
    public String toString(){
        return "username="+this.username;
    }

    public void updataTime(){
        time=System.currentTimeMillis();
    }
}
