import undetected_chromedriver as webdriver
import os
from selenium import webdriver
from seleniumbase import SB
from webdriver_manager.chrome import ChromeDriverManager
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.common.by import By
from selenium.webdriver.support.wait import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.common.action_chains import ActionChains

import json
import time

with open("rustclashdata.json", "w") as f:
    json.dump([], f)

def write_json(new_data, filename='rustclashdata.json'):
    with open(filename,'r+') as file:
        # First we load existing data into a dict.
        file_data = json.load(file)
        # Join new_data with file_data inside emp_details
        file_data.append(new_data)
        # Sets file's current position at offset.
        file.seek(0)
        # convert back to json.
        json.dump(file_data, file, indent = 4)


if __name__ == '__main__':
    email = "#"
    password = "#"


    chrome_options = webdriver.ChromeOptions()
    chrome_options.add_argument("--use_subprocess")
    chrome_options.add_argument("--disable-blink-features=AutomationControlled")
    # chrome_options.add_argument( '--headless' )

    # options.add_argument('proxy-server=106.122.8.54:3128')
    # options.add_argument('--user-data-dir=C:\Users\rober\AppData\Local\Google\Chrome\User Data\Default')

    # browser = Driver(browser="chrome")
    browser = webdriver.Chrome(service=Service(ChromeDriverManager().install()),options=chrome_options)
    browser.maximize_window()

    # chrome = Chrome()
    # browser = uc.Chrome(
    #     options=options,
    # )
    browser.execute_cdp_cmd("Page.addScriptToEvaluateOnNewDocument", {
        'source': '''
            delete window.cdc_adoQpoasnfa76pfcZLmcfl_Array;
            delete window.cdc_adoQpoasnfa76pfcZLmcfl_JSON;
            delete window.cdc_adoQpoasnfa76pfcZLmcfl_Object;
            delete window.cdc_adoQpoasnfa76pfcZLmcfl_Promise;
            delete window.cdc_adoQpoasnfa76pfcZLmcfl_Proxy;
            delete window.cdc_adoQpoasnfa76pfcZLmcfl_Symbol;
        '''
    })

    browser.get('https://steamcommunity.com/openid/loginform/?goto=%2Fopenid%2Flogin%3Fopenid.mode%3Dcheckid_setup%26openid.ns%3Dhttp%253A%252F%252Fspecs.openid.net%252Fauth%252F2.0%26openid.ns.sreg%3Dhttp%253A%252F%252Fopenid.net%252Fextensions%252Fsreg%252F1.1%26openid.sreg.optional%3Dnickname%252Cemail%252Cfullname%252Cdob%252Cgender%252Cpostcode%252Ccountry%252Clanguage%252Ctimezone%26openid.ns.ax%3Dhttp%253A%252F%252Fopenid.net%252Fsrv%252Fax%252F1.0%26openid.ax.mode%3Dfetch_request%26openid.ax.type.fullname%3Dhttp%253A%252F%252Faxschema.org%252FnamePerson%26openid.ax.type.firstname%3Dhttp%253A%252F%252Faxschema.org%252FnamePerson%252Ffirst%26openid.ax.type.lastname%3Dhttp%253A%252F%252Faxschema.org%252FnamePerson%252Flast%26openid.ax.type.email%3Dhttp%253A%252F%252Faxschema.org%252Fcontact%252Femail%26openid.ax.required%3Dfullname%252Cfirstname%252Clastname%252Cemail%26openid.identity%3Dhttp%253A%252F%252Fspecs.openid.net%252Fauth%252F2.0%252Fidentifier_select%26openid.claimed_id%3Dhttp%253A%252F%252Fspecs.openid.net%252Fauth%252F2.0%252Fidentifier_select%26openid.return_to%3Dhttps%253A%252F%252Frustclash.com%252Fapi%252Fauth%252Fsteam%252Freturn%26openid.realm%3Dhttps%253A%252F%252Frustclash.com%252Fapi%3Fopenid.mode%3Dcheckid_setup%26openid.ns%3Dhttp%253A%252F%252Fspecs.openid.net%252Fauth%252F2.0%26openid.ns_sreg%3Dhttp%253A%252F%252Fopenid.net%252Fextensions%252Fsreg%252F1.1%26openid.sreg_optional%3Dnickname%252Cemail%252Cfullname%252Cdob%252Cgender%252Cpostcode%252Ccountry%252Clanguage%252Ctimezone%26openid.ns.ax%3Dhttp%253A%252F%252Fopenid.net%252Fsrv%252Fax%252F1.0%26openid.ax.mode%3Dfetch_request%26openid.ax.type.fullname%3Dhttp%253A%252F%252Faxschema.org%252FnamePerson%26openid.ax.type.firstname%3Dhttp%253A%252F%252Faxschema.org%252FnamePerson%252Ffirst%26openid.ax.type.lastname%3Dhttp%253A%252F%252Faxschema.org%252FnamePerson%252Flast%26openid.ax.type.email%3Dhttp%253A%252F%252Faxschema.org%252Fcontact%252Femail%26openid.ax.required%3Dfullname%252Cfirstname%252Clastname%252Cemail%26openid.identity%3Dhttp%253A%252F%252Fspecs.openid.net%252Fauth%252F2.0%252Fidentifier_select%26openid.claimed_id%3Dhttp%253A%252F%252Fspecs.openid.net%252Fauth%252F2.0%252Fidentifier_select%26openid.return_to%3Dhttps%253A%252F%252Frustclash.com%252Fapi%252Fauth%252Fsteam%252Freturn%26openid.realm%3Dhttps%253A%252F%252Frustclash.com%252Fapi')

    # try:
    #     element = WebDriverWait(browser, 10).until(
    #         EC.presence_of_element_located((By.CSS_SELECTOR, "#app > div > header > div > button"))
    # )
    # finally:
    #     browser.find_element(By.CSS_SELECTOR, '#app > div > header > div > button').click()
    # time.sleep(4)
    # try:
    #     element = WebDriverWait(browser, 10).until(
    #         EC.presence_of_element_located((By.CSS_SELECTOR, '#app > div.v-dialog__container.v-dialog__container--attached.ready > div > div > div.modal.grey700.modal-content.v-card.v-sheet.theme--dark > div.v-card__text.pt-8.grey800.text-center > div.v-alert.py-3.v-sheet.theme--dark.v-alert--dense.grey600'))
    # )
    # finally:
    #     browser.find_element(By.CSS_SELECTOR, '#app > div.v-dialog__container.v-dialog__container--attached.ready > div > div > div.modal.grey700.modal-content.v-card.v-sheet.theme--dark > div.v-card__text.pt-8.grey800.text-center > div.v-alert.py-3.v-sheet.theme--dark.v-alert--dense.grey600 > div > div > p > a').click()

    try:
        element = WebDriverWait(browser, 10).until(
            EC.presence_of_element_located((By.CSS_SELECTOR, '#responsive_page_template_content > div.page_content > div:nth-child(1) > div > div > div > div.newlogindialog_FormContainer_3jLIH > div > form > div:nth-child(1) > input'))
        )
    finally:
        browser.find_element(By.CSS_SELECTOR, '#responsive_page_template_content > div.page_content > div:nth-child(1) > div > div > div > div.newlogindialog_FormContainer_3jLIH > div > form > div:nth-child(1) > input').send_keys('palevic69')
        browser.find_element(By.CSS_SELECTOR, '#responsive_page_template_content > div.page_content > div:nth-child(1) > div > div > div > div.newlogindialog_FormContainer_3jLIH > div > form > div:nth-child(2) > input').send_keys('pizdec300')
        browser.find_element(By.CSS_SELECTOR, '#responsive_page_template_content > div.page_content > div:nth-child(1) > div > div > div > div.newlogindialog_FormContainer_3jLIH > div > form > div.newlogindialog_SignInButtonContainer_14fsn > button').click()
    time.sleep(2)
    try:
        element = WebDriverWait(browser, 10).until(
            EC.presence_of_element_located((By.CSS_SELECTOR, '#imageLogin'))
        )
    finally:
        browser.find_element(By.CSS_SELECTOR, '#imageLogin').click()

    # time.sleep(2021) #capcha here
    # Done with login to web
    try:
        element = WebDriverWait(browser, 10).until(
            EC.presence_of_element_located((By.CSS_SELECTOR, '#__next > div.__className_1751e1.css-26it18 > header.css-fb0pna > div.css-1ff36h2 > div > div > div.css-2t18mx.egk5poh0 > div.css-u9hz6d > button.css-m2jpxx'))
        )
    finally:
        browser.find_element(By.CSS_SELECTOR, '#__next > div.__className_1751e1.css-26it18 > header.css-fb0pna > div.css-1ff36h2 > div > div > div.css-2t18mx.egk5poh0 > div.css-u9hz6d > button.css-m2jpxx').click()
    try:
        element = WebDriverWait(browser, 10).until(
            # EC.presence_of_element_located((By.CSS_SELECTOR, '#headlessui-tabs-panel-\:rg\: > div > div > div:nth-child(1) > div.css-1cfq7ri > button'))
            EC.presence_of_element_located((By.XPATH, "//*[starts-with(@id, 'headlessui-tabs-panel-')]/div/div/div[1]/div[2]/button"))
        )
    finally:
        browser.find_element(By.XPATH, "//*[starts-with(@id, 'headlessui-tabs-panel-')]/div/div/div[1]/div[2]/button").click()

    #Inside the withraw list
    #trying to scrape

    try:
        element = WebDriverWait(browser, 10).until(
            EC.presence_of_element_located((By.XPATH, "//*[starts-with(@id, 'headlessui-tabs-panel-')]/div/div/div[1]/div/div[2]/div/div/div"))
        )
    finally:
        time.sleep(1)
        elem_list= browser.find_element(By.XPATH, "//*[starts-with(@id, 'headlessui-tabs-panel-')]/div/div/div[1]/div/div[2]/div/div/div")

        item_names = elem_list.find_elements(By.XPATH, '//*[@id="headlessui-tabs-panel-:rg:"]/div/div/div[1]/div/div[2]/div/div/div/div/div/div[2]/div[1]/div[2]')
        item_price = elem_list.find_elements(By.XPATH, '//*[@id="headlessui-tabs-panel-:rg:"]/div/div/div[1]/div/div[2]/div/div/div/div/div/div[2]/div[2]/div/span')
        item_names_full_list = []
        item_prices_full_list = []

        for i in range(len(item_names)):
            item_names_full_list.append(item_names[i].text)
            item_prices_full_list.append(item_price[i].text)
    time.sleep(0.2)
    item_count = 0
    scrollLoop = True
    item_to_scroll_to = item_names[-1]
    browser.execute_script("arguments[0].scrollIntoView(true); window.scrollBy(0, -window.innerHeight / 2);", item_to_scroll_to)

    element = WebDriverWait(browser, 10).until(
        EC.presence_of_element_located((By.XPATH, "//*[starts-with(@id, 'headlessui-tabs-panel-')]/div/div/div[1]/div/div[2]/div/div/div"))
    )

    time.sleep(0.1)
    elem_list = browser.find_element(By.XPATH, "//*[starts-with(@id, 'headlessui-tabs-panel-')]/div/div/div[1]/div/div[2]/div/div/div")

    item_names = elem_list.find_elements(By.XPATH, '//*[@id="headlessui-tabs-panel-:rg:"]/div/div/div[1]/div/div[2]/div/div/div/div/div/div[2]/div[1]/div[2]')
    item_price = elem_list.find_elements(By.XPATH, '//*[@id="headlessui-tabs-panel-:rg:"]/div/div/div[1]/div/div[2]/div/div/div/div/div/div[2]/div[2]/div/span')
    while scrollLoop:
        try:

            if len(item_names) < 35 or len(item_price) < 35:
                scrollLoop = False
                break

            for i in range(len(item_names) - 10):
                item_names_full_list.append(item_names[i + 10].text)
                item_prices_full_list.append(item_price[i + 10].text)

            item_count = item_count + 25
            if item_count > 10000:
                scrollLoop = False

            item_to_scroll_to = item_names[-1]
            browser.execute_script("arguments[0].scrollIntoView(true); window.scrollBy(0, -window.innerHeight / 2);", item_to_scroll_to)

            element = WebDriverWait(browser, 10).until(
                EC.presence_of_element_located((By.XPATH, "//*[starts-with(@id, 'headlessui-tabs-panel-')]/div/div/div[1]/div/div[2]/div/div/div"))
            )

            time.sleep(0.1)
            elem_list = browser.find_element(By.XPATH, "//*[starts-with(@id, 'headlessui-tabs-panel-')]/div/div/div[1]/div/div[2]/div/div/div")

            item_names = elem_list.find_elements(By.XPATH, '//*[@id="headlessui-tabs-panel-:rg:"]/div/div/div[1]/div/div[2]/div/div/div/div/div/div[2]/div[1]/div[2]')
            item_price = elem_list.find_elements(By.XPATH, '//*[@id="headlessui-tabs-panel-:rg:"]/div/div/div[1]/div/div[2]/div/div/div/div/div/div[2]/div[2]/div/span')
        # except (TimeoutException, StaleElementReferenceException) as e:
        #     # Handle TimeoutException (if element not found within 10 seconds) and StaleElementReferenceException
        #     # (if the referenced element is no longer attached to the DOM)
        #     print(f"Exception occurred: {e}")
        #     break
        except Exception as e:
            # Handle other exceptions
            print(f"Unhandled exception occurred: {e}")
            break

    print("System out, scarping Ended!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")

    items_dict = {}
    for name, price in zip(item_names_full_list, item_prices_full_list):
        if name in items_dict:
            items_dict[name]["count"] += 1
        else:
            items_dict[name] = {"price": price, "count": 1}

    # Convert the dictionary to a list of dictionaries
    result_list = [{"name": name, "price": item["price"], "amount": item["count"]} for name, item in items_dict.items()]

    # Write the result to a JSON file
    file_path = os.path.join(os.path.dirname(__file__), "rustclashdata.json")
    with open(file_path, "w") as json_file:
        json.dump(result_list, json_file, indent=2)
    # write_json(result_list)
