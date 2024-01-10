"use client";
import React, {useState} from "react";
import ButtonList from "@/app/buttons";
import axios from 'axios';
import Loading from "react-loading";
import {app_const} from "@/app/const";
import CSGOItemCard from "@/app/csgo/CSGOItemCard";

export interface ItemsPageProps {
    data: AggregatedCSGOItems[]; // Declare the 'data' prop with the appropriate type
}

const CSGOItemsPage: React.FC<ItemsPageProps> = ({ data }) => {
    const [isLoading, setIsLoading] = useState(false)
    const [fetchedData, setFetchedData] = useState<AggregatedCSGOItems[]>(data)

    const doUpdate = () => {
        setIsLoading(true)
        axios.get(app_const.urls.csgo_update, {headers: {"Access-Control-Allow-Origin": "*"}})
            .then((onfulfilled) => {
                console.log(onfulfilled)
            }).catch((onrejected) => {
            console.error('Error fetching data:', onrejected)
        }).finally(() => {
            setIsLoading(false)
        })
    }

    const doFetch = () => {
        setIsLoading(true)
        axios.get(app_const.urls.csgo_aggregated_fetch, {headers: {"Access-Control-Allow-Origin": "*"}})
            .then((onfulfilled) => {
                const data = onfulfilled.data
                setFetchedData(data)
                console.log(data)
            }).catch((onrejected) => {
            console.error('Error fetching data:', onrejected)
        }).finally(() => {
            setIsLoading(false)
        })
    }

    const doFetchByTrader = (trader: string) => {
        setIsLoading(true)
        axios.get(app_const.urls.csgo_aggregated_fetch + "/" + trader, {headers: {"Access-Control-Allow-Origin": "*"}})
            .then((onfulfilled) => {
                const data = onfulfilled.data
                setFetchedData(data)
                console.log(data)
            }).catch((onrejected) => {
            console.error('Error fetching data:', onrejected)
        }).finally(() => {
            setIsLoading(false)
        })
    }

    const handleButtonClick = async (buttonText: string) => {
        if (buttonText === 'Update database') {
            doUpdate()
        } else if (buttonText === 'Fetch stored data') {
            doFetch()
        } else if (buttonText === 'CSTRADE' || buttonText === 'RAPIDSKINS') {
            doFetchByTrader(buttonText)
        }
    }

    return (
        <div style={pageStyles}>
            <h1 style={titleStyles}>CSGO Items</h1>
            <ButtonList
                buttons={['Update database', 'Fetch stored data', 'CSTRADE', 'RAPIDSKINS']}
                onClick={handleButtonClick}
                isLoading={isLoading}
            />
            <div style={aggregatedItemsContainerStyles}>
                {isLoading ? (
                    <div style={loadingStyles}>
                        <Loading type="spin" color="#007BFF" height={50} width={50}/>
                    </div>
                ) : fetchedData.map((aggregatedItems, index) => (
                    <div key={index} style={aggregatedItemsStyles}>
                        <h2 style={profitabilityStyles}>Profitability: {aggregatedItems.profitability}</h2>
                        <div style={csgoItemsContainerStyles}>
                            {aggregatedItems.items.map((item, itemIndex) => (
                                <CSGOItemCard key={itemIndex} item={item}/>
                            ))}
                        </div>
                    </div>
                ))}
            </div>
        </div>
    )
}

export default CSGOItemsPage;

// Styles
const loadingStyles = {
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    height: '200px',
}

const pageStyles = {
    maxWidth: '80%',
    margin: '0 auto',
    padding: '20px',
}

const titleStyles = {
    textAlign: 'center',
    fontSize: '32px',
    fontWeight: 'bold',
    color: '#007BFF',
    marginBottom: '20px',
    textTransform: 'uppercase',
}

const aggregatedItemsContainerStyles = {
    display: 'inline',
    flexWrap: 'wrap',
    width: "100%",
    justifyContent: 'center',
}

const aggregatedItemsStyles = {
    flexBasis: '300px', // Adjust this value as needed to control the width of each AggregatedCSGOItems
    margin: '10px',
    border: '1px solid #ddd',
    borderRadius: '5px',
    padding: '10px',
}

const profitabilityStyles = {
    backgroundColor: '#6c6c6c',
    padding: '10px',
    borderRadius: '5px',
}

const csgoItemsContainerStyles = {
    display: 'flex',
    flexWrap: 'wrap',
}