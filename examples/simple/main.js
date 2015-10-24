// This function will be called after authorization
window.onAuth = function(state, response){

    var github = new Github({
        token: response.access_token,
        auth: "oauth"
    });

    // Show user info
    var user = github.getUser();
    user.show("", function(err, info){
        var $user = $("#user").empty();
        $user.append("<img class='avatar' src='"+info.avatar_url+"'>");
        $user.append($("<div class='info'/>")
            .append("<div class='name'>" + info.name + "</div>")
            .append("<div class='login'>" + info.login + "</div>")
            .append("<div class='location'>" + info.location + "</div>"));
    });

    // Show user repositories
    user.repos(null, function(err, repos){
        window.repos = repos;
        var $ul = $("<ul/>");
        $("#repos").empty().append($ul);
        repos.forEach(function(repo){
            $ul.append("<li>" + repo.name + " <i>(" + repo.description + ")</i></li>");
        })
    });
};

// On click on 'Auth' button - open popup with redirection to authorization page
document.getElementById('btnAuth').addEventListener('click', function() {
    var clientId = '3b717f44eee01271305c';
    var redirectUri = 'https://oauth-proxy-9000.appspot.com/callback/' + clientId;

    // this value should be generated randomly and checked for each user after authorization
    var state = '57823423';

    var url = 'https://github.com/login/oauth/authorize'
        + '?client_id=' + clientId + ''
        + '&redirect_uri=' + redirectUri
        + '&scope=public_repo&state=' + state;
    var windowName = '_blank';
    var windowFeatures = 'height=500,width=500';
    window.pop = window.open(url, windowName, windowFeatures);
});