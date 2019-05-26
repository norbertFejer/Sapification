<div class="center topicText">

    <div class="input-group mb-3">
        <input id="topicTitle" type="text" class="form-control topicInput" placeholder="Notification title..." aria-label="Notification title..." aria-describedby="basic-addon1">
    </div>

    <div class="input-group">
        <div class="input-group-prepend">
            <span class="input-group-text">Message</span>
        </div>
        <textarea id="topicText" class="form-control topicInput" aria-label="With textarea"></textarea>
    </div>

    <div>
        <select id="topicDropdown" class="btn" style="border-color: #e7e7e7; margin-top: 15px;">
        </select>
    </div> 

    <div class="sendNotificatinBtn">
        <button type="button" class="btn btn-outline-dark" onclick="resetInputFields()">Reset</button>
        <button type="button" class="btn btn-outline-dark" onclick="sendNotification()">Send notification</button>
    </div>

</div>