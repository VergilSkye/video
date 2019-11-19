import { Component, Input } from '@angular/core';
import { StreamManager } from 'openvidu-browser';

@Component({
  selector: 'jhi-user-video',
  styles: [
    `
      jhi-ov-video {
        width: 100%;
        height: auto;
        float: left;
        cursor: pointer;
      }
      div div {
        position: absolute;
        background: #f8f8f8;
        padding-left: 5px;
        padding-right: 5px;
        color: #777777;
        font-weight: bold;
        border-bottom-right-radius: 4px;
      }
      p {
        margin: 0;
      }
    `
  ],
  template: `
    <div>
      <jhi-ov-video [streamManager]="streamManager"></jhi-ov-video>
      <div>
        <p>{{ getNicknameTag() }}</p>
      </div>
    </div>
  `
})
export class UserVideoComponent {
  @Input()
  streamManager: StreamManager;

  getNicknameTag() {
    // Gets the nickName of the user
    console.info(this.streamManager.stream.connection.data);
    return JSON.parse(this.streamManager.stream.connection.data).clientData;
  }
}
