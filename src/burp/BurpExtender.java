package burp;

import java.awt.Component;
import java.awt.Menu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.plaf.FileChooserUI;

import org.omg.CORBA.PUBLIC_MEMBER;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import burp.IBurpExtender;
import burp.IBurpExtenderCallbacks;
import burp.IExtensionHelpers;
import burp.ITab;

public class BurpExtender implements IBurpExtender, ITab, IContextMenuFactory, ActionListener{
	public PrintWriter stdout;
	private PrintWriter stderr;
    public IExtensionHelpers hps;
    public IBurpExtenderCallbacks cbs;

    public JPanel mainJPanel;
    
    private JTextArea rspBodyArea;
    private JScrollPane jsp0;
    private JScrollPane jsp1;
    
    private JTextArea infoExtractResultArea;
    
	private JRadioButton jrb0;
	private JRadioButton jrb1;
	
	private JButton extractButton;
	
	private JTextField oneNestJsonField;
	private JTextField twoNestJsonField;
	private JTextField threeNestJsonField;
	
	private JTextField regexField; 
	
	
	private JButton cleanRspBodyButton;
	private JButton cleanExtractResultButton;
	private JButton exportExtractResultButton;
	
	
	private IHttpRequestResponse[] selectedItems;
    
    

    @Override
    public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks)         {

        callbacks.setExtensionName("burp-info-extractor");

        this.hps = callbacks.getHelpers();
        this.cbs = callbacks;
        
        this.stdout = new PrintWriter(callbacks.getStdout(), true);
        this.stderr = new PrintWriter(callbacks.getStderr(), true);
        

        this.stdout.println("BIE(burp-info-extractor) loaded!");
        this.stdout.println("Author:LSA");
        this.stdout.println("https://github.com/theLSA/burp-info-extractor");
        
        //callbacks.registerContextMenuFactory(new Menu());
        
        callbacks.registerContextMenuFactory(this);
        
       
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                mainJPanel = new JPanel();
                mainJPanel.setLayout(null);
                
                rspBodyArea = new JTextArea();
                jsp0 = new JScrollPane(rspBodyArea);
                
                infoExtractResultArea = new JTextArea();
                jsp1 = new JScrollPane(infoExtractResultArea);
                
                jrb0 = new JRadioButton("json");
                jrb0.setSelected(true);
            	jrb1 = new JRadioButton("misc");
        		ButtonGroup group = new ButtonGroup();
        		group.add(jrb0);
        		group.add(jrb1);
        		
        		extractButton = new JButton("extract");
        		
        		oneNestJsonField = new JTextField("one");
        		twoNestJsonField = new JTextField();
        		threeNestJsonField = new JTextField();
        		
        		regexField = new JTextField("\"regex\":\"(.*?)\"");
        		
        		
        		cleanRspBodyButton = new JButton("cleanRspBody");
        		cleanExtractResultButton = new JButton("clean");
        		exportExtractResultButton = new JButton("export");
        		
        		
        		
        		/*
        		cleanRspBodyButton.setActionCommand("cleanrspbody");
            	cleanRspBodyButton.addActionListener(BurpExtender);
            	
            	cleanExtractResultButton.setActionCommand("cleanextractresult");
            	cleanExtractResultButton.addActionListener();
            	*/
        		cleanRspBodyButton.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						rspBodyArea.setText("");
					}
				});
        		
        		cleanExtractResultButton.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						infoExtractResultArea.setText("info extract result here.");
					}
				});
         
            	exportExtractResultButton.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						String s = infoExtractResultArea.getText();
						JFileChooser fc = new JFileChooser();
						int returnval = fc.showSaveDialog(null);
						if(0==returnval) {
							File savefile = fc.getSelectedFile();
							String[] sp = s.split("[\\n]");
						
							try {
								FileWriter writeout = new FileWriter(savefile);
								for(int i=0;i<sp.length;i++) {
									writeout.write(sp[i]);
				            		writeout.write("\n");
				            	}
								
								writeout.close();
								
								JOptionPane.showMessageDialog(null,"exported successfully!");
								
							}catch(IOException ex) {
								ex.printStackTrace();
							}
							
						}else {
							return;
						}
					}
				});
        		
        		mainJPanel.add(jsp0);
        		mainJPanel.add(jsp1);
        		mainJPanel.add(jrb0);
        		mainJPanel.add(jrb1);
        		mainJPanel.add(extractButton);
        		mainJPanel.add(oneNestJsonField);
        		mainJPanel.add(twoNestJsonField);
        		mainJPanel.add(threeNestJsonField);
        		mainJPanel.add(cleanRspBodyButton);
        		mainJPanel.add(cleanExtractResultButton);
        		mainJPanel.add(exportExtractResultButton);
        		
        		mainJPanel.add(regexField);
        		

        		jsp0.setBounds(20, 20, 500, 600);
        		jsp1.setBounds(900, 20, 200, 600);
        		
        		jrb0.setBounds(550, 100, 80, 30);
        		jrb1.setBounds(550, 300, 100, 30);
        		
        		oneNestJsonField.setBounds(650, 50, 80, 30);
        		twoNestJsonField.setBounds(650, 90, 80, 30);
        		threeNestJsonField.setBounds(650, 130, 80, 30);
        		
        		extractButton.setBounds(750, 200, 80, 30);
        		
        		cleanRspBodyButton.setBounds(30, 650, 120, 30);
        		cleanExtractResultButton.setBounds(900, 650, 100, 30);
        		exportExtractResultButton.setBounds(1000, 650, 100, 30);
        		
        		regexField.setBounds(650, 300, 200, 30);

                //JButton jButton = new JButton("click me!");

                extractButton.addMouseListener(new MouseAdapter() {

                    @Override
                    public void mouseClicked(MouseEvent e){
                        stdout.println("Extracted!!!");
                    	
                    	infoExtractResultArea.setText(null);
                    	
                        
                        String rspBody1 = rspBodyArea.getText();
                        
                        stdout.println("rspBody1:" + rspBody1);
                        
                        //infoExtractResultArea.setText(rspBody1);
                        
                        if (rspBodyArea.getText().isEmpty()) {
                        	infoExtractResultArea.setText("rspBody is empty!");
                        	return;
                        }
              
                        String rspBodyContent = null;
                		
                        //-------------------------------------json format extractor--------------------------------------------
                        
                		if (jrb0.isSelected()){
                			
                			
                            /*
                            int getRspBodyContentByWrapIndex = rspBody1.indexOf("\n\n");
                            
                            stdout.println("getRspBodyContentByWrapIndex:" + getRspBodyContentByWrapIndex);
                            
                            if (getRspBodyContentByWrapIndex == -1) {
                            	rspBodyContent = rspBody1;
                            	stdout.println("rspBodyContent:" + rspBodyContent);
                            }
                            else {
                            	rspBodyContent = rspBody1.substring(rspBody1.indexOf("\n\n")+2);
                            	stdout.println("rspBodyContent:" + rspBodyContent);
                            }
                            */
                            
                            
                            
                            //delete http response's \r \n \t
                            //use { and [ to get http body
                            rspBody1 = rspBody1.trim();
                            
                            rspBody1 = rspBody1.replaceAll("\r\n|\r|\n|\t", "");
                            
                            int getRspBodyContentByWrapIndexBracket = rspBody1.indexOf("[");
                            int getRspBodyContentByWrapIndexBraces = rspBody1.indexOf("{");
                            
                            stdout.println("[:"+getRspBodyContentByWrapIndexBracket);
                            stdout.println("{:"+getRspBodyContentByWrapIndexBraces);
                            
                            if(getRspBodyContentByWrapIndexBracket==-1 && getRspBodyContentByWrapIndexBraces==-1) {
                            	stdout.println("rspBodyContent is not json data3:is not startWith {/[ and endsWith }/]");
                				return;
                            }
                            else {
                            	if(getRspBodyContentByWrapIndexBracket<getRspBodyContentByWrapIndexBraces && getRspBodyContentByWrapIndexBracket!=-1 && getRspBodyContentByWrapIndexBraces!=-1) {
                            		rspBodyContent = rspBody1.substring(getRspBodyContentByWrapIndexBracket);
                            	}
                            	if(getRspBodyContentByWrapIndexBracket>getRspBodyContentByWrapIndexBraces && getRspBodyContentByWrapIndexBraces!=-1 && getRspBodyContentByWrapIndexBracket!=-1) {
                            		rspBodyContent = rspBody1.substring(getRspBodyContentByWrapIndexBraces);
                            	}
                            }
                            
                            stdout.println("rspBodyContent:"+rspBodyContent);
                            
                			
                			if (oneNestJsonField.getText().isEmpty()) {
                				stdout.println("oneNestJson is null");
                				infoExtractResultArea.setText("oneNestJson is empty,which must have a value!");
                				return;
                			}
                			
                			else {
                				stdout.println("oneNestJson isEmpty() return false");
                			}
                			
                			/*
                			
                			if ((rspBodyContent.startsWith("{")==false) && (rspBodyContent.endsWith("}")==false) && ((rspBodyContent.startsWith("[")==false) && rspBodyContent.endsWith("]")==false)){
                				stdout.println("rspBodyContent is not json data3:is not startWith {/[ and endsWith }/]");
                				return;
                			}
                			
                			*/
                			
                			
                			
                			JsonElement jsonElement = null;
                			
                	        try {
                	            jsonElement = new JsonParser().parse(rspBodyContent);
                	        } catch (Exception e1) {
                	            stdout.println("rspBodyContent is not json data0:cause exception!");
                	            return;
                	        }
                	        if (jsonElement == null) {
                	        	stdout.println("rspBodyContent is not json data1:jsonElement=null");
                	        	return;
                	        }
                	        if ((!jsonElement.isJsonObject()) && (!jsonElement.isJsonArray())){
                	        	stdout.println("rspBodyContent is not json data2:is not JsonObject and JsonArray");
                	        	return;
                	        }
                	        
                	        String inputJsonType = null;
                	        
                	        //--------init judge--------------------------------------------
                	        
                	        if (jsonElement.isJsonObject()) {
                	        	stdout.println("jsondata is jsonobject");
                	        	inputJsonType = "jsonobject";
                	        }
                	        
                	        if(jsonElement.isJsonArray()) {
                	        	stdout.println("jsondata is jsonarray");
                	        	inputJsonType = "jsonarray";
                	        }
                	        
                	        if(jsonElement.isJsonPrimitive()) {
                	        	stdout.println("jsondata is jsonprimitive");
                	        	inputJsonType = "jsonprimitive";
                	        }
                	        
                	        //---------------------------------------------------------------

                			String oneNestJsonKey = null;
                			String twoNestJsonKey = null;
                			String threeNestJsonKey = null;
	
                			if (oneNestJsonField.getText().isEmpty() == false && twoNestJsonField.getText().isEmpty() == true) {
                				
                				oneNestJsonKey = oneNestJsonField.getText().trim();
                				
                				if(inputJsonType.equals("jsonarray")) {
                					
                				
                					JsonArray oneNestJsonArray = returnJsonArray(rspBodyContent);
                					
                					String oneNestJsonValueString = null;
                					
                					
                					for(int i = 0 ; i<oneNestJsonArray.size();i++){

                						JsonObject subJsonObject = oneNestJsonArray.get(i).getAsJsonObject();
                						oneNestJsonValueString = subJsonObject.get(oneNestJsonKey).getAsString();

                						stdout.println("data: "+ oneNestJsonValueString);
                						
                						//jsonValueList.add(jsonValue);

                						//stdout.println("data1: "+subJsonObject.get("userId").getAsString());
                						infoExtractResultArea.append(oneNestJsonValueString+'\n');

                					}
                					
                					
                					
                					stdout.println("JSON result total:"+oneNestJsonArray.size());
                					
                					
                				}
                				
                				if(inputJsonType.equals("jsonobject")) {
                					JsonObject oneNestJsonObject = returnJsonObject(rspBodyContent);
                					String oneNestJsonValueString = oneNestJsonObject.get(oneNestJsonKey).getAsString();
                					
                					stdout.println("data: "+oneNestJsonValueString);
                					infoExtractResultArea.append(oneNestJsonValueString+'\n');
                				}
                			}
                				
                			if (oneNestJsonField.getText().isEmpty()==false&&twoNestJsonField.getText().isEmpty()==false&&threeNestJsonField.getText().isEmpty()==true) {

                				oneNestJsonKey = oneNestJsonField.getText().trim();
                				twoNestJsonKey = twoNestJsonField.getText().trim();
                				
                				stdout.println(oneNestJsonKey+twoNestJsonKey);
                				
                				if(inputJsonType.equals("jsonarray")) {
                					stdout.println("2nestjson-inputjsontype is jsonarray,which is developping......(You can give this json data to github issue.)");
                					infoExtractResultArea.append("2nestjson-inputjsontype is jsonarray,which is developping......(You can give this json data to github issue.");
                				}
                				
                				if(inputJsonType.equals("jsonobject")) {
                					
                					stdout.println("2nestjson-inputjsontype is jsonobject.");
                					
                					JsonObject oneNestJsonObject = returnJsonObject(rspBodyContent);
                					JsonElement oneNestJsonValueElement = oneNestJsonObject.get(oneNestJsonKey);
                					
                					stdout.println("oneNestJsonValueElement:"+oneNestJsonValueElement);
                					
                					
                					if(oneNestJsonValueElement.isJsonArray()) {
                						stdout.println("2nestjson-oneNestJsonValueElement is jsonarray.");
                						
                						JsonArray oneNestJsonValueArray = oneNestJsonObject.get(oneNestJsonKey).getAsJsonArray();
                						String twoNestJsonValueString = null;
                    					for(int i = 0 ; i<oneNestJsonValueArray.size();i++){

                    						JsonObject subJsonObject = oneNestJsonValueArray.get(i).getAsJsonObject();
                    						twoNestJsonValueString = subJsonObject.get(twoNestJsonKey).getAsString();

                    						stdout.println("data: "+ twoNestJsonValueString);
                    						
                    						//jsonValueList.add(jsonValue);

                    						//stdout.println("data1: "+subJsonObject.get("userId").getAsString());
                    						infoExtractResultArea.append(twoNestJsonValueString+'\n');

                    					}

                    					stdout.println("JSON result total:"+oneNestJsonValueArray.size());
                    					
                					}
                					
                					if(oneNestJsonValueElement.isJsonObject()) {
                						
                						stdout.println("2nestjson-oneNestJsonValueElement is jsonobject.");
                						
                						JsonObject oneNestJsonValueObject = oneNestJsonObject.get(oneNestJsonKey).getAsJsonObject();
                						String twoNestJsonValueString = oneNestJsonValueObject.get(twoNestJsonKey).getAsString();
            							stdout.println("data: "+twoNestJsonValueString);
                    					infoExtractResultArea.append(twoNestJsonValueString+'\n');
                						/*
                						JsonElement twoNestJsonValueElement = oneNestJsonValueObject.get(twoNestJsonKey);
                						
                						if(twoNestJsonValueElement.isJsonObject()) {
                							JsonObject twoNestJsonValueObject = oneNestJsonValueObject.get(twoNestJsonKey).getAsJsonObject();
                							String twoNestJsonValueString = twoNestJsonValueObject.get(twoNestJsonKey).getAsString();
                							stdout.println("data: "+twoNestJsonValueString);
                        					infoExtractResultArea.append(twoNestJsonValueString+'\n');
                						}
                						*/
                						
                    					/*
                						if(twoNestJsonValueElement.isJsonArray()) {
                							//impossible
                						}
                						*/
                					}
                				}
                			
                			}
                			
                			if(oneNestJsonField.getText().isEmpty()==false&&twoNestJsonField.getText().isEmpty()==false&&threeNestJsonField.getText().isEmpty()==false) {
                				
                				oneNestJsonKey = oneNestJsonField.getText().trim();
                				twoNestJsonKey = twoNestJsonField.getText().trim();
                				threeNestJsonKey = threeNestJsonField.getText().trim();
                				
                				
                				stdout.println(oneNestJsonKey+twoNestJsonKey+threeNestJsonKey);
                				
                				if(inputJsonType.equals("jsonobject")) {
                					stdout.println("3nestjson in jsonobject");
                					JsonObject oneNestJsonObject = returnJsonObject(rspBodyContent);
                					JsonElement oneNestJsonValueElement = oneNestJsonObject.get(oneNestJsonKey);
                					stdout.println("oneNestJsonValue-"+oneNestJsonValueElement);
                					
                					if (oneNestJsonValueElement.isJsonObject()) {
                         	        	stdout.println("onenestjsondata is jsonobject");
                         	        	//inputJsonType = "jsonobject";
                         	        	JsonObject oneNestJsonValueObject = oneNestJsonObject.get(oneNestJsonKey).getAsJsonObject();
                         	        	//JsonObject twoNestJsonObject = returnJsonObject(oneNestJsonValueObject.toString());
                    					JsonElement twoNestJsonValueElement = oneNestJsonValueObject.get(twoNestJsonKey);
                    					stdout.println("twoNestJsonValue-"+twoNestJsonValueElement);

                    					if(twoNestJsonValueElement.isJsonObject()) {
                    						stdout.println("2nestjson-twonestjsonvalueelement is jsonobject,which is developping......(You can give this json data to github issue.)");
                    						infoExtractResultArea.append("2nestjson-twonestjsonvalueelement is jsonobject,which is developping......(You can give this json data to github issue.)");
                    					}
                    					
                    					if(twoNestJsonValueElement.isJsonArray()) {
                    						
                    						stdout.println("twonestjsondata is jsonarray");
                    						JsonArray twoNestJsonValueArray = oneNestJsonValueObject.get(twoNestJsonKey).getAsJsonArray();
                    						//JsonArray threeNestJsonArray = returnJsonArray(twoNestJsonValueArray.toString());
                        					
                        					String threeNestJsonValue = null;
                        					
                        					for(int i = 0 ; i<twoNestJsonValueArray.size();i++){

                        						JsonObject subJsonObject = twoNestJsonValueArray.get(i).getAsJsonObject();
                        						threeNestJsonValue = subJsonObject.get(threeNestJsonKey).getAsString();

                        						stdout.println("data: "+ threeNestJsonValue);
                        						
                        						//jsonValueList.add(jsonValue);

                        						//stdout.println("data1: "+subJsonObject.get("userId").getAsString());
                        						infoExtractResultArea.append(threeNestJsonValue+'\n');

                        					}

                        					stdout.println("JSON result total:"+twoNestJsonValueArray.size());
                        	
                    					}
                    					
                         	        }
                					
                					if(oneNestJsonValueElement.isJsonArray()) {
                						stdout.println("3nestjson-oneNestJsonValueElement is jsonarray,which is developping......(You can give this json data to github issue.)");
                						infoExtractResultArea.append("3nestjson-oneNestJsonValueElement is jsonarray,which is developping......(You can give this json data to github issue.)");
                					}
                         	           					
                				}
                				
                				if(inputJsonType.equals("jsonarray")) {
                					stdout.println("3nestjson-inputjsontype is jsonobject,which is developping......(You can give this json data to github issue.)");
                					infoExtractResultArea.append("3nestjson-inputjsontype is jsonobject,which is developping......(You can give this json data to github issue.)");
                				}
                				
                				//stdout.println("debug:3nestjson not in jsonobject");
               				
                			}
   			
                		}
                		
                		
                		//----------------------------misc format extractor-------------------------------
                		
                		if (jrb1.isSelected()) {
                			String regexResult = null;
                			rspBodyContent = rspBody1.trim();
                			String regexContent = regexField.getText(); 
                			
                			Pattern p = Pattern.compile(regexContent);
                			Matcher m = p.matcher(rspBodyContent);
                			int regexResultCount = 0;
                			while(m.find()) {
                				//stdout.println(m.groupCount());
                				regexResultCount = regexResultCount + 1;
                				
                				regexResult = m.group(1);
                				stdout.println(regexResult);
                				
                				infoExtractResultArea.append(regexResult+'\n');
                				
                			}
                			stdout.println("MISC result total: " + regexResultCount);
                			
                			
                		}
                		
                		//------------------------------------------------------------------------------------
                		
                    }
                    
                    

                });


                cbs.customizeUiComponent(mainJPanel);
                cbs.addSuiteTab(BurpExtender.this);
            }
            
        });
        
    	
    }

    //ITab-getTabCaption()
    @Override
    public String getTabCaption() {
        return "BIE";
    }

    //ITab-getUiComponent() 
    @Override
    public Component getUiComponent() {
        return mainJPanel;
    }
    
    
    public void actionPerformed(ActionEvent event) {
    	String command = event.getActionCommand();
    	if(command.equals("sendRSPToBIE")) {
    		stdout.println("sendRSPToBIE");
    		sendToBIETester();
    	}
    	
    }
    
    public void sendToBIETester() {
    	IHttpService httpService = selectedItems[0].getHttpService();
		byte[] response = selectedItems[0].getResponse();
		/*
		IResponseInfo responseBody = this.hps.analyzeResponse(response);
		int rsponseBodyOffset = responseBody.getBodyOffset();
		
		stdout.print("rsponseBodyOffset:"+rsponseBodyOffset);
		
		String responseBodyString = response.toString().substring(rsponseBodyOffset);
		*/

		stdout.println("stbie begin");
		stdout.println(new String(response));
		stdout.println("stbie over");
		rspBodyArea.setText(new String(response));
		
		//rspBodyArea.setText(responseBodyString);
    }

	@Override
	public List<JMenuItem> createMenuItems(IContextMenuInvocation invocation) {
		// TODO Auto-generated method stub
		selectedItems = invocation.getSelectedMessages();		
		
		List<JMenuItem> menu = new ArrayList<JMenuItem>();
		
		JMenuItem itemManualTesting = new JMenuItem("Send rsp to BIE");
		itemManualTesting.setActionCommand("sendRSPToBIE");
		itemManualTesting.addActionListener(this);
		
		menu.add(itemManualTesting);
		
		return menu;
	}
	
	
	
	public JsonArray returnJsonArray(String rspBodyContent) {
		
		@SuppressWarnings("deprecation")
		JsonParser jsonParser = new JsonParser();
		
		JsonArray jsonArray = (JsonArray)jsonParser.parse(rspBodyContent);

		return jsonArray;
	}
	
	
	public JsonObject returnJsonObject(String rspBodyContent) {
		
		@SuppressWarnings("deprecation")
		JsonParser jsonParser = new JsonParser();
		
		JsonObject jsonObject = (JsonObject)jsonParser.parse(rspBodyContent);
		
		return jsonObject;
	}
	
}
