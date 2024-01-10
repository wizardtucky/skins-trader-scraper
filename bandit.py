import undetected_chromedriver as webdriver
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

with open("data.json", "w") as f:
    json.dump([], f)

def write_json(new_data, filename='data.json'):
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

    browser.get('https://bandit.camp')

    try:
        element = WebDriverWait(browser, 10).until(
            EC.presence_of_element_located((By.CSS_SELECTOR, "#app > div > header > div > button"))
        )
    finally:
        browser.find_element(By.CSS_SELECTOR, '#app > div > header > div > button').click()
    time.sleep(4)
    try:
        element = WebDriverWait(browser, 10).until(
            EC.presence_of_element_located((By.CSS_SELECTOR, '#app > div.v-dialog__container.v-dialog__container--attached.ready > div > div > div.modal.grey700.modal-content.v-card.v-sheet.theme--dark > div.v-card__text.pt-8.grey800.text-center > div.v-alert.py-3.v-sheet.theme--dark.v-alert--dense.grey600'))
        )
    finally:
        browser.find_element(By.CSS_SELECTOR, '#app > div.v-dialog__container.v-dialog__container--attached.ready > div > div > div.modal.grey700.modal-content.v-card.v-sheet.theme--dark > div.v-card__text.pt-8.grey800.text-center > div.v-alert.py-3.v-sheet.theme--dark.v-alert--dense.grey600 > div > div > p > a').click()

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

    try:
        element = WebDriverWait(browser, 10).until(
            EC.presence_of_element_located((By.CSS_SELECTOR, '#app > div > header > div > div.user-container.d-flex.align-center > div.pa-1.wallet-pill.grey900.mr-2.rounded-pill > button'))
        )
    finally:
        browser.find_element(By.CSS_SELECTOR, '#app > div > header > div > div.user-container.d-flex.align-center > div.pa-1.wallet-pill.grey900.mr-2.rounded-pill > button').click()

    try:
        element = WebDriverWait(browser, 10).until(
            EC.presence_of_element_located((By.CSS_SELECTOR, '#app > div.v-menu__content.theme--dark.v-menu__content--fixed.menuable__content__active.rounded-lg.elevation-3 > div > div'))
        )
    finally:
        time.sleep(1)
        try:
            elem = browser.find_element(By.XPATH, '/html/body/div[1]/div[2]/div/div/div[2]').click()
            if elem.is_displayed():
                print("ELEMENT WAS FOUND !")
                elem.click() # this will click the element if it is there
        except Exception as e:
            print("...")
    time.sleep(1)
    try:
        element = WebDriverWait(browser, 10).until(
            EC.presence_of_element_located((By.CSS_SELECTOR, '#app > div.v-dialog__container.v-dialog__container--attached.ready > div > div > div.py-8.px-8.rounded-lg.modal-content.v-sheet.theme--dark.grey700 > div > div:nth-child(2) > div > div:nth-child(1) > div > div'))
        )
    finally:
        browser.find_element(By.CSS_SELECTOR, '#app > div.v-dialog__container.v-dialog__container--attached.ready > div > div > div.py-8.px-8.rounded-lg.modal-content.v-sheet.theme--dark.grey700 > div > div:nth-child(2) > div > div:nth-child(1) > div > div').click()
        time.sleep(4)

    try:
        element = WebDriverWait(browser, 10).until(
            EC.presence_of_element_located((By.CSS_SELECTOR, '#app > div.v-dialog__container.v-dialog__container--attached.ready > div > div > div.container.modal-content.modal-content.container--fluid > div > div.inv-ctn.col-md-8.col-lg-9.col-12 > div:nth-child(2) > div > div.vue-recycle-scroller__item-wrapper > div:nth-child(1) > div'))
        )
    finally:
        elem_list= browser.find_element(By.XPATH, '//*[@id="app"]/div[4]/div/div/div[1]/div/div[1]/div[2]/div/div[1]/div[1]/div')

        item_prices = elem_list.find_elements(By.XPATH, '//*[@id="app"]/div/div/div/div[1]/div/div[1]/div[2]/div/div[1]/div/div/div/div[2]/div[1]/span[1]/span')
        item_names = elem_list.find_elements(By.XPATH, '//*[@id="app"]/div[4]/div/div/div[1]/div/div[1]/div[2]/div/div[1]/div/div/div/div[2]/div[2]/div[1]/div')
        item_amount = elem_list.find_elements(By.XPATH, '//*[@id="app"]/div[4]/div/div/div[1]/div/div[1]/div[2]/div/div[1]/div/div/div/div[2]/div[3]/span[2]')
        print("Scraping items")
        for i in range(len(item_amount)):
            write_json({
                "name": item_names[i].get_attribute("innerHTML"),
                "price": item_prices[i].get_attribute("innerHTML"),
                "amount": item_amount[i].get_attribute("innerHTML"),
            })
            # print(item_names[i].get_attribute("innerHTML"), item_prices[i].get_attribute("innerHTML"), item_amount[i].get_attribute("innerHTML"))


    # loop from here, croll at the end
    website_height = 0
    scrollLoop = True
    element_to_scroll = browser.find_elements(By.CSS_SELECTOR, '#app > div.v-dialog__container.v-dialog__container--attached.ready > div > div > div.container.modal-content.modal-content.container--fluid > div > div.inv-ctn.col-md-8.col-lg-9.col-12 > div:nth-child(2) > div')
    old_item_names = item_amount

    while scrollLoop:
        #scroll
        item_to_scroll_to = item_names[-1]
        actions = ActionChains(browser)
        actions.move_to_element(item_to_scroll_to).perform()
        time.sleep(0.1)
        try:
            element = WebDriverWait(browser, 10).until(
                EC.presence_of_element_located((By.CSS_SELECTOR, '#app > div.v-dialog__container.v-dialog__container--attached.ready > div > div > div.container.modal-content.modal-content.container--fluid > div > div.inv-ctn.col-md-8.col-lg-9.col-12 > div:nth-child(2) > div > div.vue-recycle-scroller__item-wrapper > div:nth-child(1) > div'))
            )
        finally:
            elem_list= browser.find_element(By.XPATH, '//*[@id="app"]/div[4]/div/div/div[1]/div/div[1]/div[2]/div/div[1]/div[1]/div')


        #getting items
        item_prices = elem_list.find_elements(By.XPATH, '//*[@id="app"]/div/div/div/div[1]/div/div[1]/div[2]/div/div[1]/div/div/div/div[2]/div[1]/span[1]/span')
        item_names = elem_list.find_elements(By.XPATH, '//*[@id="app"]/div[4]/div/div/div[1]/div/div[1]/div[2]/div/div[1]/div/div/div/div[2]/div[2]/div[1]/div')
        item_amount = elem_list.find_elements(By.XPATH, '//*[@id="app"]/div[4]/div/div/div[1]/div/div[1]/div[2]/div/div[1]/div/div/div/div[2]/div[3]/span[2]')

        end_check = len(item_prices)
        item_prices = item_prices[len(item_amount)-14:]
        item_names = item_names[len(item_amount)-14:]
        item_amount = item_amount[len(item_amount)-14:]

        item_name_stop_loop = item_names[-1]

        #printing items
        if end_check < 63:
            print(end_check)
            for i in range(len(item_amount)):
                for j in range(len(old_item_names)):
                    if old_item_names[j].get_attribute("innerHTML") == item_names[i].get_attribute("innerHTML"):
                        no_match_found = False
                    else:
                        no_match_found = True
                        break
                if no_match_found:
                    write_json({
                        "name": item_names[i].get_attribute("innerHTML"),
                        "price": item_prices[i].get_attribute("innerHTML"),
                        "amount": item_amount[i].get_attribute("innerHTML"),
                    })
            browser.close()
            print("close program")
            break
        else:
            print(end_check, "else")
            if old_item_names[0].get_attribute("innerHTML") == item_names[0].get_attribute("innerHTML"):
                browser.close()
                print("close program")
                break
            for i in range(len(item_amount)):
                write_json({
                    "name": item_names[i].get_attribute("innerHTML"),
                    "price": item_prices[i].get_attribute("innerHTML"),
                    "amount": item_amount[i].get_attribute("innerHTML"),
                })
            old_item_names = item_names
            # print(item_names[i].get_attribute("innerHTML"), item_prices[i].get_attribute("innerHTML"), item_amount[i].get_attribute("innerHTML"))
