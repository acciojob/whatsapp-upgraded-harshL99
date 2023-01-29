package com.driver;

import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class WhatsappRepository {

    private Map<String,User> userMap=new HashMap<>();
    private Map<Group, List<User>> groupMap=new HashMap<>();
    private Map<Integer,Message> messageMap=new HashMap<>();
    private Map<User,List<Message>> userMessageMap=new HashMap<>();
    int count=0;
    public String saveUser(String name,String mobile) throws Exception{
        if(userMap.containsKey(name)) throw new Exception("User already exists");

        User newUser=new User(name,mobile);
        userMap.put(name,newUser);
        return "SUCCESS";
    }
    public Group saveGroup(List<User> users){
        if(users.size()==1) return new Group();
        String nameOfGroup="";
        if(users.size()==2){
            nameOfGroup=users.get(1).getName();
            Group newGroup=new Group(nameOfGroup,2);
            groupMap.put(newGroup,users);
            return newGroup;
        }
        count=count+1;
        nameOfGroup="Group"+" "+count;
        Group newGroup=new Group(nameOfGroup,users.size());
        groupMap.put(newGroup,users);
        return newGroup;
    }
    public int saveMessage(String content){
        if(content==null) return 0;
        int id=messageMap.size()+1;

        Message newMessage=new Message(id,content);
        messageMap.put(id,newMessage);

        return id;
    }
    public int sendMessage(Message message,User sender,Group group) throws Exception{
        if(!groupMap.containsKey(group)) throw new Exception("Group does not exist");

        boolean found=false;
        for(User user : groupMap.get(group)){
            if(user.getName().equals(sender.getName())){
                found=true;
                break;
            }
        }
        if(!found) throw new Exception("You are not allowed to send message");

        List<Message> messages=new ArrayList<>();
        if(userMessageMap.containsKey(sender)){
            messages=userMessageMap.get(sender);
        }
        messages.add(message);
        userMessageMap.put(sender,messages);//Updating the messageList adding new message with the sender...

        group.setNumberOfMessages(group.getNumberOfMessages()+1);
        return group.getNumberOfMessages();
    }
    public String changeAdmin(User approver,User user, Group group) throws Exception{
        if(!groupMap.containsKey(group)) throw new Exception("Group does not exist");
        List<User> users=groupMap.get(group);

        if(approver.getName().equals(users.get(0).getName())){
            int idx=-1;
            for(int i=1;i<users.size();i++){
                if(users.get(i).getName().equals(user.getName())){
                    idx=i;
                    break;
                }
            }
            if(idx>-1){
                User temp=users.set(0,users.get(idx));
                users.set(idx,temp);
                groupMap.put(group,users);
                return "SUCCESS";
            }
            else throw new Exception("User is not a participant");
        }
        else throw new Exception("Approver does not have rights");
    }
    public int deleteUser(User user) throws Exception{
        Group foundedGroup=null;
        int idx=0;
        for(Group group : groupMap.keySet()){
            idx=0;
            for(User users : groupMap.get(group)){
                if(users.getName().equals(user.getName())){
                    foundedGroup=group;
                    break;
                }
                idx++;
            }
            if(foundedGroup!=null) break;
        }
        if(foundedGroup==null) throw new Exception("User not found");
        if(idx==0) throw new Exception("Cannot remove admin");
        List<User> userList =groupMap.get(foundedGroup);
        userList.remove(user);
        foundedGroup.setNumberOfParticipants(foundedGroup.getNumberOfParticipants()-1);

        if(userMessageMap.containsKey(user)){
            foundedGroup.setNumberOfMessages(foundedGroup.getNumberOfMessages()-userMessageMap.get(user).size());
            userMessageMap.remove(user);
        }

        groupMap.put(foundedGroup,userList);//Updating...

        int sum= foundedGroup.getNumberOfParticipants()+ foundedGroup.getNumberOfMessages();

        for(Group group : groupMap.keySet()){
            sum+=group.getNumberOfMessages();
        }
        return sum+2;
    }
    public String findMessage(Date start,Date end,int k){
        return "";
    }
}
