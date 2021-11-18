import xml.etree.ElementTree as ET

name1 = "wiki"
name2 = "bbe"
switch = True #True for wiki

set1 = ET.parse('books/input/target_{}.xml'.format(name1 if switch else name2)).getroot()
set2 = ET.parse('books/input/target_{}.xml'.format(name2 if switch else name2)).getroot()

i = 1
with(open("books/goldstandard/gs_{}_2_{}.csv".format(name1, name2))) as res:
    res = res.readlines()
    for line in res:
        tokens = line.split(",")
        x = tokens[0][tokens[0].find("_") + 1:]
        y = tokens[1][tokens[1].find("_") + 1:]
        z = tokens[2][:-1]
        print(str(i) + "  -  " + z + "  -  " + x + "  -  " + y)
        i = i+1
        print(set1[int(x)-1][0].text + "  -  " + set1[int(x)-1].find('authors')[0].text)
        print(set2[int(y)-1][0].text + "  -  " + set2[int(y)-1].find('authors')[0].text)
        print()    