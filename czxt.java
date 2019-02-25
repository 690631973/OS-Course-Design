import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;   

class MemoryManager{
	//public short[][] memory;
	public int[] pagenumber;
	//public LinkedList<PageTable> pagelist;
	int filenum;
	MemoryManager(){
		filenum=0;
		/*memory=new short[256][128];
		pagenumber=new int[256];
		for(int i=0;i<256;i++) {
			for(int j=0;j<128;j++) {
				memory[i][j]=0;
			}	
		}*/
		pagenumber=new int[256];
		for(int i=0;i<256;i++)
			pagenumber[i]=-1;
	}
	public boolean Allocate(Process p){     //返回false表示失败  返回true表示成功
		int sum=0;
		for(int i=0;i<256;i++)
			if(pagenumber[i]==-1)
				sum+=1;
		if(sum>=16) {
			/*PageTable tmp=new PageTable();
			tmp.pid=p.pid;
			tmp.pagetable=new short[16][3];*/
			for(short i=0;i<16;i++) {
				short j;
				for(j=0;j<256;j++)
					if(pagenumber[j]==-1)
						break;
				/*tmp.pagetable[i][0]=i;
				tmp.pagetable[i][1]=j;
				tmp.pagetable[i][2]=0;*/
				p.page.add(new PageCell(i,j,0));
				pagenumber[j]=p.pid;
			}
			return true;
		}
		return false;
	}
	/*public boolean Remove(Process p) {
		for(PageTable i:pagelist) {
			if(i.pid==p.pid) {
				for(short[] j:i.pagetable) {
					pagenumber[j[1]]=-1;
					for(int k=0;k<128;k++)
						memory[j[1]][k]=0;
				}
				p.inmemory=0;
				pagelist.remove(i);
				return true;
			}
		}
		return false;
	}*/
	/*public boolean MoveInMemory(Process p) {
		try{
			if(!Allocate(p)) {
				return false;
			}
			for(PageTable i:pagelist) {
				if(i.pid==p.pid) {
					for(short[] j:i.pagetable)
						InPage(j,i.pid);
				}
			}
			FileInputStream fis = new FileInputStream("D:\\store"+p.pid+"data"+".txt");
		 
			//Construct BufferedReader from InputStreamReader
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			String line = null;
			while ((line = br.readLine()) != null) {
				String[] s=line.split(" ");
				int loc1=Integer.parseInt(s[0]);
				short loc2=(short)Integer.parseInt(s[1]);
				short value=(short)Integer.parseInt(s[2]);
				System.out.println("value:"+value+" pagenum:"+loc1+" offset:"+loc2);
				SetMemory(MMU(p.pid,loc1,loc2),value);
			}
			br.close();
			p.inmemory=1;
			return true;
		}
		catch(IOException e) {
			return false;
		}
	}
	public void EmbedMemory(Process p) {
		try {
			File file = new File("D:\\store"+p.pid+"insts"+".txt");
			if (!file.exists()) {
				file.createNewFile();
		   }
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
		    BufferedWriter bw = new BufferedWriter(fw);
		    for(String line:p.insts) {
		    	bw.write(line);
		    	bw.newLine();
		    }
		    bw.close();
		} 
		catch (IOException e) {
		    return;
		}
		try {
			File file = new File("D:\\store"+p.pid+"data"+".txt");
			if (!file.exists()) {
				file.createNewFile();
		   }
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
		    BufferedWriter bw = new BufferedWriter(fw);
		    for(PageTable pt:pagelist) {
		    	if(pt.pid==p.pid) {
		    		for(short[] i:pt.pagetable) {
		    			for(int j=0;j<128;j++) {
		    				if(memory[i[1]][j]!=0) {
		    					bw.write(i[0]+" "+j+" "+memory[i[1]][j]);
		    					bw.newLine();
		    				}
		    			}
		    		}
		    	}
		    }
		    Remove(p);
		    bw.close();
		} 
		catch (IOException e) {
		    return;
		}
	}*/
	public short[] MMU(Process p,int pagenum,short offset) throws RuntimeException{
		short[] ret=new short[2];
		for(PageCell i:p.page) {
			if(i.num==pagenum) {
				ret[0]=(short)i.addr;
				ret[1]=offset;

				System.out.println(i.addr);
				return ret;
			}
		}
		short loc=PageReplace(p,pagenum);
		ret[0]=loc;
		ret[1]=offset;
		return ret;
	}
	public void SetMemory(Process p,short[] loc,short num) {
		//memory[loc[0]][loc[1]]=num;
		int index=(int)loc[0]*128+(int)loc[1];
		p.duler.memo.get(index).data=(int)num;
		System.out.println("yezhen:"+loc[0]+"  pianyidizhi:"+loc[1]+"  xiugaichengle:"+num);
	}
	public short GetMemory(Process p,short[] loc) {
		//return memory[loc[0]][loc[1]];
		int index=(int)loc[0]*128+(int)loc[1];
		return (short)p.duler.memo.get(index).data;
	}
	public short PageReplace(Process p,int inpagenum) {
		int max=p.page.get(0).pri;
		int loc=0;
		for(int j=1;j<16;j++) {
			if(p.page.get(j).pri>max) {
				loc=j;
				max=p.page.get(j).pri;
			}
		}
		OutPage(p.page.get(loc),p);
		p.page.get(loc).num=inpagenum;
		p.page.get(loc).pri=0;
		InPage(p.page.get(loc),p);
		for(int j=0;j<16;j++) {
			if(j!=loc) {
				p.page.get(j).pri++;
			}
		}
		return (short)p.page.get(loc).addr;
	}
	public void InPage(PageCell p,Process pr) {
		try{
			FileInputStream fis = new FileInputStream("D:\\store"+pr.pid+" "+p.num+".txt");
		 
			//Construct BufferedReader from InputStreamReader
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		 
			String line = null;
			while ((line = br.readLine()) != null) {
				String[] s=line.split(" ");
				short loc=(short)Integer.parseInt(s[0]);
				short value=(short)Integer.parseInt(s[1]);
				pr.duler.memo.get(p.addr*128+loc).data=value;
			}
			br.close();
		}
		catch(IOException e) {
			System.out.println("yehao:"+p.num+"yezhen:"+p.addr+"tihuan");
			return;
		}
		System.out.println("yehao:"+p.num+"yezhen:"+p.addr+"tihuan");
	}
	public void OutPage(PageCell p,Process pr) {
		File file =new File("D:\\store"+pr.pid+" "+p.num+".txt");
        try {
            if(!file.exists()) {
                file.createNewFile();
            }
            FileWriter fileWriter =new FileWriter(file);
            fileWriter.write("");
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            return;
        }
		for(int i=0;i<128;i++) {
			if(pr.duler.memo.get(p.addr+i).data!=0) {	
				FileWriter fw = null;
				try {
					File f=new File("D:\\store"+pr.pid+" "+p.num+".txt");
					fw = new FileWriter(f, true);
				} 
				catch (IOException e) {
					e.printStackTrace();
				}
				PrintWriter pw = new PrintWriter(fw);
				pw.println(i+" "+pr.duler.memo.get(p.addr+i).data);
				pw.flush();
			}
		}
		for(int i=0;i<128;i++) {
			pr.duler.memo.get(p.addr+i).data=0;
		}
		System.out.println("yehao:"+p.num+"yezhen:"+p.addr+"beitihuan");
	}
}