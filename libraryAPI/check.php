<?php
// include our OAuth2 Server object
require_once __DIR__.'/server.php';

// Handle a request to a resource and authenticate the access token
if (!$server->verifyResourceRequest(OAuth2\Request::createFromGlobals())) {
    $server->getResponse()->send();
    die;
}

echo json_encode($server->getResourceController()->getToken());

// echo json_encode(['success' => true, 'message' => 'testing response']);
