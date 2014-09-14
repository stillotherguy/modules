package operation;

import base.BaseConnection;
import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;

import java.util.List;

/**
 * Created by zhangjing on 14-9-8.
 */
public class OprateGroup extends BaseConnection{

    public void join(String groupName, String memberName) throws KeeperException, InterruptedException {
        String path = "/" + groupName + "/" +memberName;
        String createdPath = zk.create(path, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        System.out.println("Created " + path);
    }

    public void list(String groupName) throws KeeperException, InterruptedException {
        String path = "/" + groupName;

        try{
            //?
            List<String> children = zk.getChildren(path, false);
            if(children.isEmpty()){
                System.out.printf("No members in group %s\n", groupName);
                //??
                System.exit(1);
            }
            for(String child:children){
                System.out.println(child);
            }
        }catch (KeeperException.NoNodeException e){
            System.out.printf("Group %s does not existed\n", groupName);
            System.exit(1);
        }
    }

    public void testLambda(){
    }
}
