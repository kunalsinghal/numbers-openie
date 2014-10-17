import nltk
import sys
index = 1 
for line in sys.stdin:
    try: 
        text = nltk.word_tokenize(line.strip())
        tagged = nltk.pos_tag(text)
        print index
        index += 1
        print line.strip()
        print tagged
        print
        print
        print
    except:
        pass 
        
