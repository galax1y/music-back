Project created for INF01120 @ UFRGS

Simplest API for music creation via text - can also process .mid and .txt file inputs
Transforms this things into .mid and .wav and serve them to the front-end
Stores files locally, uses the InMemoryDatabase pattern - so when the app restarts, everything is flushed - don't know, don't care.

Mostly used this opportunity to learn a little bit about Java Spring Boot

Want to run this project in your own machine? Download and set up your synthesizer on `src/synthesizer` and `MidiToWavConverter` class (hard-coded, because... oh well...).

Download and run the front-end (https://github.com/galax1y/galax1y-music-front)[here].

Stack/libs:
Java Spring for the backend
React with Shadcn components for the UI
Tailwind for stylization
Tone.js for playing music w/ JS
Vite for bundling the JavaScript bloating shenanigans
