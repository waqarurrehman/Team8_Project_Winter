import requests
from bs4 import BeautifulSoup
import csv
import time

headers = {
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:94.0) Gecko/20100101 Firefox/94.0',
    'Cookie': 'XXX'
}

with open("C:\\Users\\Felix\Desktop\\target_fdb.csv", 'a', newline='') as csvfile:
    writer = csv.writer(csvfile, delimiter='|')

    for year in range(1900, 2000):
        try:
            page = requests.get("https://www.fictiondb.com/search/searchresults.php?author=&title=&series=&isbn=&datepublished={}&synopsis=&rating=-&anthology=&imprint=0&pubgroup=0&myrating=-&list=-&status=-&dateread=&srchtxt=multi&styp=6&awardY=on&majpubY=on&GN9001N=on".format(year), headers=headers, timeout=10)
        except requests.exceptions.RequestException as e:
            print("skip")
            continue
        soup = BeautifulSoup(page.content, 'html.parser')
        table = soup.find(id='srtauthlist')
        if(table is None):
            continue
        table_body = table.find('tbody')
        rows = table_body.find_all('tr')
        print(len(rows))
        for row in rows:
            cols = row.find_all('td')
            cols = [ele.text.strip() for ele in cols]
            if(len(cols) > 1):
                writer.writerow([cols[0], cols[1], cols[4], cols[5], cols[6]])
        
        time.sleep(1)

with open("C:\\Users\\Felix\Desktop\\target_fdb.csv", 'a', newline='') as csvfile:
    writer = csv.writer(csvfile, delimiter='|')

    for year in range(2006, 2017):
        print(year)
        for month in ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec']:
            try:
                page = requests.get("https://www.fictiondb.com/search/searchresults.php?author=&title=&series=&isbn=&datepublished={}-{}&synopsis=&rating=-&anthology=&imprint=0&pubgroup=0&myrating=-&list=-&status=-&dateread=&srchtxt=multi&styp=6&awardY=on&majpubY=on&GN9001N=on".format(month, year), headers=headers, timeout=10)
            except requests.exceptions.RequestException as e:
                print("skip")
                continue
            soup = BeautifulSoup(page.content, 'html.parser')
            table = soup.find(id='srtauthlist')
            if(table is None):
                continue
            table_body = table.find('tbody')
            rows = table_body.find_all('tr')
            print(len(rows))
            for row in rows:
                cols = row.find_all('td')
                cols = [ele.text.strip() for ele in cols]
                if(len(cols) > 1):
                    writer.writerow([cols[0], cols[1], cols[4], cols[5], cols[6]])
            
            time.sleep(1)
        