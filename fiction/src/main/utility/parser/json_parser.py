#!/usr/python
import os
import sys
import time
import zipfile
import json
import glob
import csv
########################################################################
# Name: json_parser.py                                                 #
#                                                                      #
# Description: Parsing Json twitter file and writes to csv/property    #
#                                                                      #
#                                                                      #
# Author: Subash Prakash (OVGU)                                        #
########################################################################

# Directory Name
dir_name = os.path.dirname(sys.argv[0])

# Current Date
curr_date = time.strftime("%Y%m%d")

# Absolute Directory Name
abs_dir_name=os.getcwd()

# Log Filename
only_log_fname = "json_parser_" + curr_date + ".log"

def write_to_log(logFName,type,message):
    file_writer = open(logFName,"a+")
    if type == 1:
        file_writer.write(" INFO: " + message + "\n")
    elif type == -1:
        file_writer.write(" ERROR: " + message + "\n")
#Steps:
#1. Read the zip file and get the json. For each json open a method to read it and return important Infos Use that info to 
#write into a csv or prop file and then move to processsed dir the zip with appending currentdate

#Define Case class
dict_case = {"csv":1,"prop":2}
def case(cond):
    if(cond not in dict_case):
        tmp_file_handler.write("Condition given is not satisfied, please check and correct it according to the options!!!")
        sys.exit(-1) 
    return dict_case[cond]

tweets=[]
def readAndWriteJson(fname):
    #print(fname)
    
    try:
        with open(fname,"rb") as file:
            jsonFile = json.load(file)
            #Creation of a new tweets
            tweet_id = (jsonFile["id"])
            tweet = (jsonFile["text"].encode("ascii", errors="ignore").decode())
            tweet_tuple = (str(tweet_id),str(tweet.strip("\n\t")))
            tweets.append(tweet_tuple)
        file.close()
    except IOError:
        write_to_log(only_log_fname, -1, "Could not open the file")
        sys.exit(-1)
        
   
    
    if len(tweets)!=0:
        return 1,tweets
    else:
        return 0


def simplyWriteToFile(tweets,out, flag):
    
    if(flag!=2):
        for val in tweets:
            out.write(val[0] + "," + val[1] + "\n")
        out.close()
    else:
        q=0
        for val in tweets:
            out.write(str(q) + ".q.id=" + val[0] + "\n" + str(q) + ".q.text=" + val[1] + "\n")
            q+=1
        out.close()
        

####### Start of main() ################
tmp_file_handler = open(only_log_fname,'w')
tmp_file_handler.write("Execution Started \n")
flag=0

try:
    import json_parser_config as jpc
except ImportError:
    tmp_file_handler.write("Import error, looks like the configuration file is not present")
    

tmp_file_handler.close()

write_to_log(only_log_fname, 1, "Checking on zip to start the process")

if len(jpc.inputDir) == 0:
    write_to_log(only_log_fname, -1, "Looks like configurations are empty. Please look into the  configurations")
    sys.exit(-1)


###Some Preprocess###########
zips = []


if "," in jpc.zipNames:
    print("Entered")
    zipNames = jpc.zipNames
    zips = zipNames.split(",")

### Start of ZipFile reading ################
if len(zips)!=0:
    for name in zips:
        write_to_log(only_log_fname, 1, "Started Reading --> " + name )
        with zipfile.ZipFile(jpc.zipFileLocation + "/" + name + ".zip") as zi:
            zi.extractall(jpc.inputDir)
            zi.close()
        files = glob.glob(jpc.inputDir + "/" + name + "/*.txt")
        for file in files:
            sc,tweets = readAndWriteJson(file)
            #print(tweets)
            if(sc == 1):
                flag = case(jpc.option)
                if(flag == 1):
                    out = open("output.csv","w")
                elif(flag == 2):
                    out = open("tune2.properties","w")
                #Typically default but can be defined
                else:
                    out = open("output.txt","w")
            simplyWriteToFile(tweets, out, flag)
                    
                