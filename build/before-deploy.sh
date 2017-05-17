#!/usr/bin/env bash
if [ "$TRAVIS_BRANCH" = 'master' ] && [ "$TRAVIS_PULL_REQUEST" == 'false' ]; then
    openssl aes-256-cbc -K $encrypted_2400e9f7a35e_key -iv $encrypted_2400e9f7a35e_iv -in build/codesigning.asc.enc -out build/codesigning.asc -d
    gpg --fast-import build/codesigning.asc
fi
