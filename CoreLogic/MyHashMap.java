    import java.util.*;

    public class MyHashMap<k, v> {
          // this is the basic node structure inside the linkedList
         private static class Node<k,v>{
             public k key;
             public v value;
             Node <k,v> next;
             public Node(k key, v value) {
                 this.key = key;
                 this.value = value;
             }
         }
         // what else we need
        private List<Node<k,v>>buckets=new ArrayList<>();
        int size;
        public MyHashMap(int capacity) {
            buckets=new ArrayList<>(Collections.nCopies(capacity, null));
            size=0;
        }
        // now we have list and inside that initalized value not first oopeartion will be put
        // before that we have to get the index at which we are trying to insert
        private int getIdx(k key){
            // first we have get hascode then we have mathFloormod
            return Math.floorMod(Objects.hash(key), buckets.size());
        }
        // now we will do the put oopeartion
        public void put(k key, v value){
            int idx=getIdx(key);
            Node<k,v>current=buckets.get(idx);
            while(current!=null){
                if(Objects.equals(current.key,key)){
                    current.value=value;
                    return ;
                }
                current=current.next;
            }
            Node<k,v> newNode=new Node<>(key,value);
            newNode.next=buckets.get(idx);
            buckets.set(idx,newNode);
            size++;
        }
        // to get we will find the node stroed for the current one and we will check if that is presenmt or not if not we will return null
        public  v get(k key){
             int idx=getIdx(key);
             Node<k,v>current=buckets.get(idx);
             while(current!=null){
                 if(Objects.equals(current.key,key)){
                     return current.value;
                 }
                 current=current.next;
             }
             return null;
        }
        public  v remove(k key){
            int idx=getIdx(key);
            Node<k,v> current=buckets.get(idx);
            Node<k,v> prev=null;
            while(current!=null){
                if(Objects.equals(current.key,key)){
                    if(prev==null){
                        buckets.set(idx,current.next);
                    }
                    else{
                        prev.next=current.next;
                    }
                    size--;
                    return current.value;
                }
                prev=current;
                current=current.next;
            }
           return null;

        }

      public int getSize(){
            return size;
      }


    }