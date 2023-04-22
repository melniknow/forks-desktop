import httpx
from fastapi import BackgroundTasks, FastAPI
from typing import Annotated

from fastapi import Query

import asyncio

forks = {}
is_set_task = False

app = FastAPI()

uri_token = 'c9af132a2632cb74c1f59d524dbbb5b2'
main_link = 'http://api.oddscp.com:8111/forks?bk2_name=bet365,pinnacle,188bet&token=' + uri_token


async def request(client):
    response = await client.get(main_link)
    return response.json()


async def update_forks():
    async with httpx.AsyncClient() as client:
        result = await request(client)
        global forks
        forks = result

async def get_forks_from_main_server():
    while True:
        await asyncio.sleep(2)
        await update_forks()


@app.get('/forks')
def get_forks(is_middles: str, min_cf: float, max_cf: float, min_fi: float, max_fi: float, alive_sec: float,
              token: str, background_tasks: BackgroundTasks,
              bk2_name: Annotated[list[str] | None, Query()] = None,
              bet_types: Annotated[list[str] | None, Query()] = None):
    if uri_token != token:
        return

    # --workers 1
    global is_set_task
    if not is_set_task:
        print("Added Task")
        is_set_task = True
        background_tasks.add_task(get_forks_from_main_server)

    local_forks = forks.copy()

    result = []
    for fork in local_forks:
        if fork['alive_sec'] < alive_sec:
            continue

        names = str(bk2_name[0]).split(',')
        types = str(bet_types[0]).split(',')

        if (not fork['BK1_name'] in names) or (not fork['BK2_name'] in names):
            continue

        if not fork['BK1_bet_type'] in types or not fork['BK2_bet_type'] in types:
            continue

        if str(fork['is_middles']) == str(is_middles) and min_cf < fork['BK1_cf'] < max_cf \
                and min_cf < fork['BK2_cf'] < max_cf and min_fi <= fork['income'] <= max_fi:
            result.append(fork)

    return result
