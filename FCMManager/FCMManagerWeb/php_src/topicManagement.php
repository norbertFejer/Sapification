<div>
    <div>
        <div class="leftDiv">

            <div id="topicManagementOption" class="footerOptions">
                <div class="input-group mb-3 ttt">
                    <input id="newTopicName" type="text" class="form-control" placeholder="Topic name..." aria-label="Recipient's username" aria-describedby="basic-addon2">
                    <div class="input-group-append">
                        <button id="addTopic" class="btn btn-outline-dark" type="button" onclick="addTopic()">Add</button>
                        <button id="manageTopics" class="btn btn-outline-dark" onclick="manageTopics()">Manage</button>
                    </div>
                </div>
            </div>

            <div class="topicDiv">
                <ul id="topicList" class="list-group">

                </ul>
            <div>

        </div>
    </div>
</div>


<div id="collapsable" class="rightDiv" style="display: none;">
    <div style="height: 55px;">
        <button class='btn btn-outline-dark float-right collapseBtn' onclick='hideTopicManagement()'>Close</button>
    </div>
    <div>
        <div class="topicDiv">
            <ul id="topicManagementList" class="list-group">
                
            </ul>
        <div>
    </div>
</div>