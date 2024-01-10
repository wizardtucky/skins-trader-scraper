import React from "react";
import CSGOItemsPage from "@/app/csgo/CSGOItemsPage";
import {app_const} from "@/app/const";
import {use} from "react";

async function getData() {
    const res = await fetch(app_const.urls.csgo_aggregated_fetch)
    const items = await res.json()
    return items
}

export default async function Page() {
    const data: AggregatedCSGOItems[] = await getData()
    console.log(data)
    return <CSGOItemsPage data={data}/>
}